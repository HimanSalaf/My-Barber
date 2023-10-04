package com.example.mybarber;

public class NotificationDetails {
    private String title;
    private String message;

    public NotificationDetails() {
        // Default constructor required for Firebase
    }

    public NotificationDetails(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}

