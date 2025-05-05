package com.TodayTask.Admin.Panel.serviceImpl;

public class FileUploadResponse {
    private String fileName;

    // Default constructor for serialization
    public FileUploadResponse() {}

    public FileUploadResponse(String fileName) {
        this.fileName = fileName;
    }

    // Getters and setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
