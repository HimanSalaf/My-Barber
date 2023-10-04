package com.example.mybarber;

public class ConfirmationData {
    private String bookingId;
    private String CustomerName;
    private String CustomerMobile;
    private String SelectedDate;
    private String SelectedTimeSlot;
    private String BarberName;
    private String ShopAddress;
    private String BarberServices;
    private String TotalPrice;

    public ConfirmationData() {
        // Default constructor required for Firebase serialization
    }

    public ConfirmationData(String bookingId,String CustomerName,String CustomerMobile,String SelectedDate,String SelectedTimeSlot,String BarberName,String ShopAddress,String BarberServices,String TotalPrice) {
        this.bookingId = bookingId;
        this.CustomerName = CustomerName;
        this.CustomerMobile=CustomerMobile;
        this.SelectedDate = SelectedDate;
        this.SelectedTimeSlot = SelectedTimeSlot;
        this.BarberName = BarberName;
        this.ShopAddress= ShopAddress;
        this.BarberServices = BarberServices;
        this.TotalPrice = TotalPrice;
    }

    // Getters and setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public String getTextViewName() {
        return CustomerName;
    }

    public void setTextViewName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public String getTextViewPhone() {
        return CustomerMobile;
    }

    public void setTextViewPhone(String CustomerMobile) {
        this.CustomerMobile = CustomerMobile;
    }

    public String getTextViewDate() {
        return SelectedDate;
    }

    public void setTextViewDate(String SelectedDate) {
        this.SelectedDate = SelectedDate;
    }

    public String getTextViewTimeSlot() {
        return SelectedTimeSlot;
    }

    public void setTextViewTimeSlot(String SelectedTimeSlot) {
        this.SelectedTimeSlot = SelectedTimeSlot;
    }

    public String getTextViewBarber() {
        return BarberName;
    }

    public void setTextViewBarber(String BarberName) {
        this.BarberName = BarberName;
    }

    public String getTextViewAddress() {
        return ShopAddress;
    }

    public void setTextViewAddress(String ShopAddress) {
        this.ShopAddress = ShopAddress;
    }

    public String getTextViewServices() {
        return BarberServices;
    }

    public void setTextViewServices(String BarberServices) {
        this.BarberServices = BarberServices;
    }

    public String getTextViewTotal() {
        return TotalPrice;
    }

    public void setTextViewTotal(String TotalPrice) {
        this.TotalPrice = TotalPrice;
    }

    public void setCancelled(boolean b) {
    }
}


