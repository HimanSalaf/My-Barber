package com.example.mybarber;

public class Appointment {
    private String bookingId;
    private String name;
    private String phone;
    private String date;
    private String time;
    private String barber;
    private String address;

    public Appointment() {
        // Default constructor required for Firebase
    }
    public Appointment(String bookingId, String name, String phone, String date, String time, String barber, String address) {
        this.bookingId = bookingId;
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.barber = barber;
        this.address = address;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getBarber() {
        return barber;
    }

    public String getAddress() {
        return address;
    }
}



