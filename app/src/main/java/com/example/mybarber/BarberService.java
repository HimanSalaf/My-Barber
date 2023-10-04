package com.example.mybarber;

public class BarberService {
    private String name;
    private int icon;
    private double price;

    public BarberService(String name, int icon,double price) {
        this.name = name;
        this.icon = icon;
        this.price=price;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
    public double getPrice() {
        return price;
    }
}

