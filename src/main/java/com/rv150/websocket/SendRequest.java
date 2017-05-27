package com.rv150.websocket;

/**
 * Created by ivan on 15.05.17.
 */
public class SendRequest {
    private String fileName;
    private long fileSize;
    private String receiverId;

    public SendRequest() {}

    public SendRequest(String fileName, long fileSize, String receiverId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.receiverId = receiverId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}

