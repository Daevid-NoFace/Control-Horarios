package model;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller2 {

    public static void mergeExcelFiles(List<FileInputStream> list) throws IOException {
        File file = new File("Calendar.xlsx");
        XSSFWorkbook book = new XSSFWorkbook();

        System.out.println("Ruta del file -> " + file.getAbsolutePath());
        XSSFSheet sheet = null;

        int count = 0; //para saber por cual excel van

        for (int i = 0; i < list.size(); i++) {
            FileInputStream fileInputStream = list.get(i);
            XSSFWorkbook auxWorkbook = new XSSFWorkbook(fileInputStream);

            System.out.println("Excel #" + ++count);
            int count2 = 0; //para saber por cual hoja van

            for (int j = 0; j < auxWorkbook.getNumberOfSheets(); j++) {

                System.out.println("Hoja #" + ++count2);

                sheet = book.createSheet(auxWorkbook.getSheetAt(j).getSheetName());
                copySheets(book, sheet, auxWorkbook.getSheetAt(j));
            }
        }

        try {
            writeFile(book, file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** @param newWorkbook -> libro que se esta creando
     *  @param newSheet -> hoja que se esta creando
     * @param sheet -> hoja a copiar
     *
     *  el booleano es como si fuera papel carbon
     *
     */
    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet) { //este metodo cambia el estilo de copia por el valor booleano
        copySheets(newWorkbook, newSheet, sheet, true); //el booleano cambia el tipo de copia (true: formato y colores, false: formato y no colores)
    }

    /**
     *   los parametros aqui son lo mismo
     */
    private static void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet, boolean copyStyle){
        int newRowNumber = newSheet.getLastRowNum() + 1;    //la verdad esto me sobra, pero lo dejare por si acaso, pq ni se incrementa ni da color
        int maxColumnNum = 0;

        Map<Integer, XSSFCellStyle> styleMap = (copyStyle) ? new HashMap<>() : null;

        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow srcRow = sheet.getRow(i);   //tomo la fila correspondiente para copiarla
            XSSFRow destRow = newSheet.createRow(i + newRowNumber);

            if (srcRow != null) {   //si la fila a copiar tiene instancia
                //metodo de copiar la fila
                copyRow(newWorkbook, sheet, newSheet, srcRow, destRow, styleMap);

                if (srcRow.getLastCellNum() > maxColumnNum) { //este if es para saber el ancho de la fila a copiar
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));    //esto es para ajustar los anchos de las celdas
        }
    }

    public static void copyRow(XSSFWorkbook newWorkbook, XSSFSheet srcSheet, XSSFSheet destSheet, XSSFRow srcRow, XSSFRow destRow, Map<Integer, XSSFCellStyle> styleMap) {
        destRow.setHeight(srcRow.getHeight());

        for (int i = srcRow.getFirstCellNum(); i <= srcRow.getLastCellNum(); i++) {
            if (i > 0) {
                XSSFCell oldCell = srcRow.getCell(i);
                XSSFCell newCell = destRow.getCell(i);

                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(i);
                    }
                    copyCell(newWorkbook, oldCell, newCell, styleMap);
                }
                //poner el fondo blanco


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
                System.out.println(oldCell.getRowIndex() + "" + oldCell.getColumnIndex());
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
