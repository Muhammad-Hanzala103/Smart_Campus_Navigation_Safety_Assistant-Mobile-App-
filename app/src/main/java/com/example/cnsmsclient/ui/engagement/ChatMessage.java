package com.example.cnsmsclient.ui.engagement;

public class ChatMessage {
    String message;
    boolean isUser;
    long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }
}
