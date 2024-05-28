package com.spring.importdatademo.utilities;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExcelUtils {
    // Common method to read header and create header map
    public static Map<String, Integer> readHeaderMap(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
            headerMap.put(headerRow.getCell(cellNum).getStringCellValue().trim(), cellNum);
        }
        return headerMap;
    }

    // Common method to process Excel data
    public static void processExcelData(MultipartFile file, BiConsumer<Row, Map<String, Integer>> rowProcessor) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> headerMap = readHeaderMap(sheet);
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    rowProcessor.accept(row, headerMap);
                }
            }
        }
    }
}
