package com.example.mybarber;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class DoorstepConfirmation extends AppCompatActivity {

    private String bookingId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorstep_confirmation);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not signed in
            return;
        }

        // Get the user ID
        userId = currentUser.getUid();
        // Initialize views
        TextView textViewName = findViewById(R.id.usernameText);
        TextView textViewPhone = findViewById(R.id.mobileText);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView barberText = findViewById(R.id.barber_text);
        TextView addressText = findViewById(R.id.address_text);
        Button doorConfirmButton = findViewById(R.id.confirm_button);

        // Retrieve appointment details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String customerName = sharedPreferences.getString("username", "");
        String phone = sharedPreferences.getString("phone", "");
        String selectedDate = sharedPreferences.getString("selectedDoorstepDate", "");
        String selectedTime = sharedPreferences.getString("selectedDoorstepTime", "");
        String selectedBarber = sharedPreferences.getString("selectedDoorstepBarber", "");
        String selectedAddress = sharedPreferences.getString("DoorstepAddress", "");

        // Set the appointment details in the TextViews
        textViewName.setText("Customer Name: " + customerName);
        textViewPhone.setText("Customer Mobile: " + phone);
        dateText.setText("Appointment date: " + selectedDate);
        timeText.setText("Appointment time: " + selectedTime);
        barberText.setText("Appointed Barber: " + selectedBarber);
        addressText.setText("Customer Address: " + selectedAddress);

        //sets up a notification channel for devices running Android Oreo (API level 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", "notificationMessage", importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Create an explicit intent to launch the notification activity
        Intent intent1 = new Intent(DoorstepConfirmation.this, Notification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(DoorstepConfirmation.this, 0, intent1, PendingIntent.FLAG_MUTABLE);

        // Handle the confirmation button click
        doorConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointmentToFirebase(customerName, phone, selectedDate, selectedTime, selectedBarber, selectedAddress);
                // Create a notification manager
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(DoorstepConfirmation.this);
                // Create a notification builder
                NotificationCompat.Builder builder = new NotificationCompat.Builder(DoorstepConfirmation.this, "default")
                        .setSmallIcon(R.drawable.baseline_notifications_active_24)
                        .setContentTitle("Appointment Confirmed")
                        .setContentText("Your appointment has been confirmed.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                // Save the notification details to Firebase
                saveNotificationToFirebase(userId, bookingId, "Appointment Confirmed", "Your doorstep appointment has been confirmed.");

                // Show the notification
                if (ContextCompat.checkSelfPermission(DoorstepConfirmation.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DoorstepConfirmation.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
                }
                notificationManager.notify(1, builder.build());

                Intent intent = new Intent(DoorstepConfirmation.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void saveAppointmentToFirebase(String customerName, String phone, String selectedDate, String selectedTime, String selectedBarber, String selectedAddress) {
        // Generate a unique booking ID
        bookingId = UUID.randomUUID().toString();

        // Create an Appointment object with the selected appointment details
        Appointment appointment = new Appointment(bookingId, customerName, phone, selectedDate, selectedTime, selectedBarber, selectedAddress);

        // Save the Appointment object to the Realtime Database under the current user ID with the bookingId as the child reference
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("DoorStep Confirmations").child(userId).child(bookingId);
        appointmentsRef.setValue(appointment);
    }
    private void saveNotificationToFirebase(String userId, String bookingId, String notificationTitle, String notificationMessage) {
        // Save the notification details to Firebase
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications").child(this.userId).child(bookingId);
        NotificationDetails notificationDetails = new NotificationDetails(notificationTitle, notificationMessage);
        notificationsRef.setValue(notificationDetails);
    }
}

