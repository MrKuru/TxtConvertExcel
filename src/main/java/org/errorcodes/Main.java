package org.errorcodes;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        List<ErrorInfo> errorList = getErrorList("***"); //your path

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Error Codes");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Code");
            headerRow.createCell(2).setCellValue("Message");
            headerRow.createCell(3).setCellValue("Stat");

            int rowNum = 1;
            for (ErrorInfo error : errorList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(error.getName());
                row.createCell(1).setCellValue(error.getCode());
                row.createCell(2).setCellValue(error.getMessage());
                row.createCell(3).setCellValue(error.getStat());
            }

            try (FileOutputStream fileOut = new FileOutputStream("error-codes.xlsx")) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<ErrorInfo> getErrorList(String filePath) {
        List<ErrorInfo> errorList = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(filePath));

            Pattern pattern = Pattern.compile("\\b(\\w+)\\(\"([A-Z_0-9]+)\", \"(.*?)\", (HttpStatus\\.[A-Z_]+)\\),");  //your pattern
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1);
                String code = matcher.group(2);
                String message = matcher.group(3);
                String stat = matcher.group(4);

                ErrorInfo error = new ErrorInfo(name, code, message, stat);
                errorList.add(error);
                System.out.println("Name: " + name + ", Code: " + code + ", Message: " + message + ", Stat: " + stat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorList;
    }
}

class ErrorInfo {
    private String name;
    private String code;
    private String message;
    private String stat;

    public ErrorInfo(String name, String code, String message, String stat) {
        this.name = name;
        this.code = code;
        this.message = message;
        this.stat = stat;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStat() {
        return stat;
    }
}
