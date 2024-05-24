package org.tuanit.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.tuanit.annotation.excel.export.ExportExcel;
import org.tuanit.annotation.excel.imports.MapField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelUtil {
    public static <T> List<T> sheetToObjectWithAsync(Sheet sheet, Class<T> tClass) {
        try {
            List<T> results = Collections.synchronizedList(new ArrayList<>());//tao danh sach T list
            Iterator<Row> rows = sheet.iterator();//tao row
            int rowNumber = 0;
            List<String> header = new ArrayList<>();//tao header

            Map<String, Field> fieldMap = buildMapFieldWithAnnotation(tClass);

            while (rows.hasNext()) {//duyệt từng row

                Row row = rows.next();//lay row hien tai
//                if (!isValidRow(row))
//                    continue;
                if (rowNumber == 0) {//bo qua header
                    rowNumber++;
                    Iterator<Cell> cells = row.iterator();//get cell trong row;
                    while (cells.hasNext()) {
                        header.add(cells.next().getStringCellValue());// get lấy header
                    }
                    continue;
                }

                try {
                    int cellId = 0;
                    T tObj = tClass.newInstance();

                    while (cellId < row.getLastCellNum()) {//lap cell include field null
                        Cell cell = row.getCell(cellId);

                        if (Objects.isNull(cell)) {
                            cellId++;
                            continue;
                        }

                        Field field = fieldMap.get(header.get(cellId).toLowerCase());

                        if (field == null) {
                            cellId++;
                            continue;
                        }
                        if (field.getAnnotation(MapField.class).booleanType()) {
                            field.set(tObj, Objects.equals(getValueCell(cell, field), "X"));
                        } else {
                            field.set(tObj, getValueCell(cell, field));
                        }

                        cellId++;
                    }
                    results.add(tObj);
                    //return tObj;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Cấu trúc excel không đúng !!!");
                }
//                    return null;
            }

            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public static Object getValueCell(Cell cell, Field field) {
        DataFormatter dataFormatter = new DataFormatter();
        String valueStr = "";
        valueStr = dataFormatter.formatCellValue(cell);
        if (valueStr.isEmpty())
            return null;
        switch (field.getType().getName().replace("java.lang.", "")) {
            case "Integer":
                return Integer.parseInt(valueStr);
            case "Long":
                return Long.parseLong(valueStr);
            case "Float":
                return Float.parseFloat(valueStr);
            case "Double":
                return Double.parseDouble(valueStr);
        }
        return valueStr;
    }

    public static <T> ByteArrayInputStream toExcel(List<T> tList, List<String> headers, String sheetName, Class<T> tClass) {
        if (tList.size() == 0)
            throw new RuntimeException("Not element to export Excel!!!");
        try (Workbook workbook = new SXSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            //Create Header
            Row rowHeader = sheet.createRow(0);
            int cellHeaderId = 0;
            for (String item : headers) {
                Cell cell = rowHeader.createCell(cellHeaderId);
                cell.setCellValue(item);
                cellHeaderId++;
            }
            if (tClass.isAnnotationPresent(ExportExcel.class)) {
                Map<String, Field> mapField = buildMapField(tClass);
                int rowId = 1;
                for (T item : tList) {
                    Row row = sheet.createRow(rowId);
                    int cellIds = 0;
                    for (String itemHeader : headers) {
                        Cell cell = row.createCell(cellIds);

                        Field field = mapField.get(itemHeader.toLowerCase());
                        if (field != null && field.get(item) != null) {
                            cell.setCellValue(field.get(item).toString());
                        }

                        cellIds++;
                    }
                    rowId++;
                }
            } else
                throw new RuntimeException("Not Annotation @ExportExcel in Class");
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("fail to export data to Excel file: " + e.getMessage());
        }
    }

    private static <T> Map<String, Field> buildMapField(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).collect(Collectors.toConcurrentMap(field -> field.getName().toLowerCase(), field -> {
            field.setAccessible(true);
            return field;
        }));
    }

    private static <T> Map<String, Field> buildMapFieldWithAnnotation(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(MapField.class)).collect(Collectors.toConcurrentMap(field -> {
            return field.getAnnotation(MapField.class).value().equals("") ? field.getName().toLowerCase() : field.getAnnotation(MapField.class).value().toLowerCase();
        }, field -> {
            field.setAccessible(true);
            return field;
        }));
    }
}
