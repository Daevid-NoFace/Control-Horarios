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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    public static void mergeExcelFiles(File file, List<FileInputStream> list) throws IOException {
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
        int total_sheets =0;
        for (FileInputStream fin : list) {
            XSSFWorkbook b = new XSSFWorkbook(fin);
            for (int i = 0; i < b.getNumberOfSheets(); i++) {
                sheet = book.createSheet(b.getSheetName(i));
                copySheets(book, sheet, b.getSheetAt(i));
                total_sheets++;
                if(book.getNumberOfSheets() == 1){
                    calendar_year = book.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
                    calendar_year = calendar_year.split(" ")[0];
                    location = book.getSheetAt(0).getRow(0).getCell(6).getStringCellValue();
                }
                if(total_sheets>1) {
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
                    pict.resize(3,3);
                    Cell cell =sheet.getRow(53).getCell(6);
                    if(cell !=null){
                        cell.setCellValue(Integer.parseInt(calendar_year));
                        cell.setCellType(CellType.NUMERIC);
                        System.out.println(cell.getNumericCellValue());
                    }

                    cell = sheet.getRow(53).getCell(1);
                    if(cell !=null) {
                        cell.setCellValue("En "+location+" a");
                        cell.setCellType(CellType.STRING);
                    }
                }

            }

        }

        try {

            writeFile(book, file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet){
        copySheets(newWorkbook, newSheet, sheet, true);
    }

    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet, boolean copyStyle){
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
            if(j>0){
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
        if(styleMap != null) {
            int stHashCode = oldCell.getCellStyle().hashCode();
            XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
            if(newCellStyle == null){
                newCellStyle = newWorkbook.createCellStyle();
                newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                styleMap.put(stHashCode, newCellStyle);
            }
            newCell.setCellStyle(newCellStyle);
        }
        switch(oldCell.getCellTypeEnum()) {
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
}
