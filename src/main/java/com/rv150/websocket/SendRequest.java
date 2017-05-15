package com.rv150.websocket;

/**
 * Created by ivan on 15.05.17.
 */
public class SendRequest {
    private String fileName;
    private String receiverId;

    public SendRequest(String receiverId, String fileName) {
        this.fileName = fileName;
        this.receiverId = receiverId;
    }

    public SendRequest() {

    }

    public String getFileName() {
        return fileName;
    }

    public String getReceiverId() {
        return receiverId;
    }
}

