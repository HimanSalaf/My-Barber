package com.example.mybarber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmationActivity extends AppCompatActivity {
    private TextView textViewTotal;
    private FirebaseDatabase database;
    private DatabaseReference confirmationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Confirmation");

        // Initialize TextView elements
        TextView textViewName = findViewById(R.id.usernameText);
        TextView textViewPhone = findViewById(R.id.mobileText);
        TextView textViewDate = findViewById(R.id.dateText);
        TextView textViewTimeSlot = findViewById(R.id.slotText);
        TextView textViewBarber = findViewById(R.id.barberText);
        TextView textViewAddress = findViewById(R.id.addressText);
        TextView textViewServices = findViewById(R.id.servicesText);
        textViewTotal = findViewById(R.id.totalText);

        // Retrieve data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "");
        String phone = sharedPreferences.getString("phone", "");
        String date = sharedPreferences.getString("selectedDate", "");
        String timeSlot = sharedPreferences.getString("selectedTimeSlot", "");
        String barber = sharedPreferences.getString("selectedBarberName", "");
        String shopAddress = sharedPreferences.getString("selectedAddress", "");
        String selectedServices = sharedPreferences.getString("selectedServicesText", "");
        // Set the retrieved data to TextView elements
        textViewName.setText("Customer Name: " + name);
        textViewPhone.setText("Customer Mobile: " + phone);
        textViewDate.setText("Selected Date: " + date);
        textViewTimeSlot.setText("Selected Time Slot: " + timeSlot);
        textViewBarber.setText("Barber Name: " + barber);
        textViewAddress.setText("Shop Address: " + shopAddress);
        textViewServices.setText("Preferred Services: " + selectedServices);

        float totalPrice = Float.parseFloat(sharedPreferences.getString("totalPrice", "0.00"));
        textViewTotal.setText("Total Price: RS " + String.format("%.2f", totalPrice));


        database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            confirmationRef = database.getReference("Confirmations").child(userId);
        }

        Button confirmToPay = findViewById(R.id.confirmToPayBtn);
        confirmToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Phone=String.valueOf(phone);
                String TotalPrice = String.valueOf(totalPrice);

                // Inside the confirmToPay button onClickListener
                String bookingId = confirmationRef.push().getKey(); // Generate a unique booking ID
                ConfirmationData confirmationData = new ConfirmationData(bookingId, name,Phone, date, timeSlot, barber, shopAddress, selectedServices, TotalPrice);
                confirmationRef.child(bookingId).setValue(confirmationData);

                Intent intent = new Intent(ConfirmationActivity.this, Home.class);
                intent.putExtra("source", "confirm");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

