package model;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

public class Controller {

    public static void mergeExcelFiles(File file, List<FileInputStream> list) throws IOException {
        ArrayList<String> cell_formulas = generateCellToFormula();

        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = null;

        //FileInputStream obtains input bytes from the image file
        InputStream inputStream = new FileInputStream("src/resources/palobiofarma.png");
        //Get the contents of an InputStream as a byte[].
        byte[] bytes = IOUtils.toByteArray(inputStream);
        //Adds a picture to the workbook
        int pictureIdx = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        //close the input stream
        inputStream.close();


        //to control the sheet where paste the picture
        String calendar_year = "";
        String location = "";
        int total_sheets = 0;
        try {
            for (FileInputStream fin : list) {
                XSSFWorkbook b = new XSSFWorkbook(fin);
                for (int i = 0; i < b.getNumberOfSheets(); i++) {
                    sheet = book.createSheet(b.getSheetName(i));
                    copySheets(book, sheet, b.getSheetAt(i));
                    total_sheets++;
                    if (book.getNumberOfSheets() == 1) {
                        calendar_year = book.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
                        calendar_year = calendar_year.split(" ")[0];
                        location = book.getSheetAt(0).getRow(0).getCell(6).getStringCellValue();
                    }
                    if (total_sheets > 1) {
                        //LOGO Creation
                        //Returns an object that handles instantiating concrete classes
                        CreationHelper helper = book.getCreationHelper();

                        //Creates the top-level drawing patriarch.
                        Drawing drawing = sheet.createDrawingPatriarch();

                        //Create an anchor that is attached to the worksheet
                        ClientAnchor anchor = helper.createClientAnchor();
                        //set top-left corner for the image
                        anchor.setCol1(1);
                        anchor.setRow1(1);

                        //Creates a picture
                        Picture pict = drawing.createPicture(anchor, pictureIdx);
                        //Reset the image to the original size
                        pict.resize(3, 3);

                        //Push date
                        Cell cell = sheet.getRow(53).getCell(6);
                        if (cell != null) {
                            cell.setCellValue(Integer.parseInt(calendar_year));
                            cell.setCellType(CellType.NUMERIC);
                            System.out.println(cell.getNumericCellValue());
                        }

                        cell = sheet.getRow(53).getCell(1);
                        if (cell != null) {
                            cell.setCellValue("En " + location + " a");
                            cell.setCellType(CellType.STRING);
                        }

                        cell = sheet.getRow(48).getCell(6);
                        if (cell != null) {
                            //-2 para qiue coincida el numero de la lista con el numero de la hoja
                            cell.setCellFormula("'" + book.getSheetAt(0).getSheetName() + "'" + "" +
                                    cell_formulas.get(total_sheets - 2));
                            System.out.println(cell.getCellFormula());
                        }

                        //passWeekendsToSheets
                        passWeekendToSheets(book, total_sheets);
                    }
                }
            }

       /* Cell cell = book.getSheet("1").getRow(48).getCell(6);
        if(cell != null){
            cell.setCellFormula("'2021'!H11");
        }*/


            writeFile(book, file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet) {
        copySheets(newWorkbook, newSheet, sheet, true);
    }

    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet, boolean copyStyle) {
        int newRownumber = newSheet.getLastRowNum() + 1;
        int maxColumnNum = 0;
        Map<Integer, XSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, XSSFCellStyle>() : null;

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            newSheet.addMergedRegion(region);
        }

        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow srcRow = sheet.getRow(i);
            XSSFRow destRow = newSheet.createRow(i + newRownumber);
            if (srcRow != null) {
                copyRow(newWorkbook, sheet, newSheet, srcRow, destRow, styleMap);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
    }

    public static void copyRow(XSSFWorkbook newWorkbook, XSSFSheet srcSheet, XSSFSheet destSheet, XSSFRow srcRow, XSSFRow destRow, Map<Integer, XSSFCellStyle> styleMap) {
        destRow.setHeight(srcRow.getHeight());
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            if (j > 0) {
                XSSFCell oldCell = srcRow.getCell(j);
                XSSFCell newCell = destRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }
                    copyCell(newWorkbook, oldCell, newCell, styleMap);
                }
            }

        }
    }

    public static void copyCell(XSSFWorkbook newWorkbook, XSSFCell oldCell, XSSFCell newCell, Map<Integer, XSSFCellStyle> styleMap) {
        if (styleMap != null) {
            int stHashCode = oldCell.getCellStyle().hashCode();
            XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
            if (newCellStyle == null) {
                newCellStyle = newWorkbook.createCellStyle();
                newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                styleMap.put(stHashCode, newCellStyle);
            }
            newCell.setCellStyle(newCellStyle);
        }
        switch (oldCell.getCellTypeEnum()) {
            case STRING:
                newCell.setCellValue(oldCell.getRichStringCellValue());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case BLANK:
                newCell.setCellType(CellType.BLANK);
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    protected static void writeFile(XSSFWorkbook book, File file) throws Exception {
        FileOutputStream out = new FileOutputStream(file);
        book.write(out);
        out.close();
    }

    private static ArrayList<String> generateCellToFormula() {
        ArrayList<String> cells = new ArrayList<>();
        cells.add("!H11");
        cells.add("!P11");
        cells.add("!X11");
        cells.add("!H21");
        cells.add("!P21");
        cells.add("!X21");
        cells.add("!H30");
        cells.add("!P30");
        cells.add("!X30");
        cells.add("!H40");
        cells.add("!P40");
        cells.add("!X40");

        return cells;
    }

    private static void passWeekendToSheets(XSSFWorkbook workbook, int indexSheet) {

        //indexSheet begins on 2
        //System.out.println("Pass weekend, total of sheets -> " + workbook.getNumberOfSheets());
        //System.out.println("Pass weekend, name of sheet -> " + workbook.getSheetName(indexSheet - 2));

        ArrayList<Integer> dateOfTheWeekends = null;

        if ((indexSheet - 1) == 1) {    //January
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 4;
                int dayWeekend = (i == 0) ? 1 : 7;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 2) {    //Febrery
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 4;
                int dayWeekend = (i == 0) ? 9 : 15;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 3) {    //Febrery
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 4;
                int dayWeekend = (i == 0) ? 17 : 23;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 4) {    //Febrery
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 14;
                int dayWeekend = (i == 0) ? 1 : 7;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 45; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 5) {    //Febrery
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 14;
                int dayWeekend = (i == 0) ? 9 : 15;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 6) {    //Febrery
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 14;
                int dayWeekend = (i == 0) ? 17 : 23;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 45; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 7) {    //July
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 24;
                int dayWeekend = (i == 0) ? 1 : 7;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 8) {    //August
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 24;
                int dayWeekend = (i == 0) ? 9 : 15;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 9) {    //September
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 24;
                int dayWeekend = (i == 0) ? 17 : 23;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 45; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 10) {    //October
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 33;
                int dayWeekend = (i == 0) ? 1 : 7;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 11) {    //November
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 33;
                int dayWeekend = (i == 0) ? 9 : 15;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 45; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }

        if ((indexSheet - 1) == 12) {    //December
            for (int i = 0; i < 2; i++) {   //only sunday and saturday

                dateOfTheWeekends = new ArrayList<>();
                boolean end = false;
                XSSFSheet calendarSheet = workbook.getSheetAt(0);
                int rowStart = 33;
                int dayWeekend = (i == 0) ? 17 : 23;

                while (!end) {

                    XSSFRow srcRow = calendarSheet.getRow(rowStart);
                    XSSFCell cell = srcRow.getCell(dayWeekend);

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        System.out.println("Date -> " + date.getDate());

                        dateOfTheWeekends.add(date.getDate());
                    } else if (cell == null || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                        end = true;

                    rowStart++;
                }

                //coincidir las fechas en la hoja de horarios
                XSSFSheet scheduleSheet = workbook.getSheetAt(indexSheet - 1);
                System.out.println("Name of Sheet-> " + workbook.getSheetAt(indexSheet - 1).getSheetName());

                //pararse en la columna de los dias, columna B(index 1) fila 16 (index 15)
                //iterar por filas
                for (int j = 15; j < 46; j++) {
                    XSSFRow row = scheduleSheet.getRow(j);
                    System.out.println("j-> " + j);
                    if (row.getCell(1) != null) {
                        System.out.println("Cell -> " + row.getCell(1).getNumericCellValue());
                        XSSFCell cell = row.getCell(1);

                        if (cell.getCellType() == CellType.NUMERIC)
                            if (!dateOfTheWeekends.isEmpty() && cell.getNumericCellValue() == dateOfTheWeekends.get(0)) {
                                cell.setCellValue(999999);
                                dateOfTheWeekends.remove(0);
                            }
                    }
                }
            }
        }
    }

    private static void passWeekendToSheets(XSSFWorkbook workbook) {
        XSSFSheet calendarSheet = workbook.getSheetAt(0);
        int startRow = 0;
        int startColumn = 0;

        for (int i = 0; i < 3; i++) {   //recorrer filas
            for (int j = 0; j < 4; j++) {   //recorrer columnas

            }
        }

        //recorrer los meses
        for (int i = 1; i < 13; i++) {

        }
    }
}
