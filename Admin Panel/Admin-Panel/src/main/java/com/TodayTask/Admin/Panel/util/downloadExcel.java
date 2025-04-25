package com.TodayTask.Admin.Panel.util;


import com.TodayTask.Admin.Panel.Entity.UserEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class downloadExcel {
    public static ByteArrayOutputStream downloadUsersExcel(List<UserEntity> users) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row firstRow = sheet.createRow(0);
        firstRow.createCell(0).setCellValue("ID");
        firstRow.createCell(1).setCellValue("Name");
        firstRow.createCell(2).setCellValue("Username");
        firstRow.createCell(3).setCellValue("Email");
        firstRow.createCell(4).setCellValue("Date of Birth");
        firstRow.createCell(5).setCellValue("Gender");
        firstRow.createCell(6).setCellValue("Address");
        firstRow.createCell(7).setCellValue("Profile Image");
        firstRow.createCell(8).setCellValue("Contact Number");
        firstRow.createCell(9).setCellValue("Pincode");
        firstRow.createCell(10).setCellValue("Role");

        // Add user data
        int rowIndex = 1;
        for (UserEntity user : users) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getUserName());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getDob() != null ? user.getDob().toString() : "N/A");
            row.createCell(5).setCellValue(user.getGender() != null ? user.getGender().toString() : "N/A");
            row.createCell(6).setCellValue(user.getAddress());
            row.createCell(7).setCellValue(user.getProfileImage() != null ? user.getProfileImage() : "N/A");
            row.createCell(8).setCellValue(user.getContactNumber());
            row.createCell(9).setCellValue(user.getPincode());
            row.createCell(10).setCellValue(user.getAccessRole() != null ? user.getAccessRole().toString() : "N/A");
        }

        workbook.write(baos);
        workbook.close();
        return baos;
    }

}
