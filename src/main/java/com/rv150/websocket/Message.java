package com.rv150.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ivan on 10.05.17.
 */

public class Message {
    @JsonProperty("type")
    private String type;
    @JsonProperty("data")
    private String data;

    public static final String INITIALIZE_USER = "GettingID";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDING_FINISHED = "sending_finished";
    public static final String ERROR = "error";


    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }
    public String getData() {
        return data;
    }

    Message() { }

    Message(String type, String data) {
        this.type = type;
        this.data = data;
    }
}

