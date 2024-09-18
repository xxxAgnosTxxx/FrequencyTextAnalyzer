package org.prokhorovgm.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataService {
    private static final int AVERAGE_WORD_COUNT = 10;

    public static List<String> readWords(String excelFile, int sheetNumber) {
        try {
            FileInputStream fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(sheetNumber);
            List<String> data = new ArrayList<>(sheet.getLastRowNum() * AVERAGE_WORD_COUNT);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    data.add(cell.getRichStringCellValue().getString());
                }
            }
            fis.close();

            return data.stream()
                .map(d -> d.split(" "))
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File not found by the path: %s", excelFile));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can`t read excelFile by the path: %s", excelFile));
        }
    }
}
