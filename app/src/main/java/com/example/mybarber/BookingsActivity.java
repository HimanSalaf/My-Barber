package com.example.mybarber;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {
    private ListView listViewBookings;
    private List<ConfirmationData> bookingsList;
    private FirebaseDatabase database;
    private DatabaseReference confirmationRef;
    private BookingAdapter bookingAdapter;
    private ValueEventListener bookingsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Barber Booking");

        // Initialize ListView
        listViewBookings = findViewById(R.id.listViewBookings);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            confirmationRef = database.getReference("Confirmations").child(userId);
        }

        // Initialize bookingsList
        bookingsList = new ArrayList<>();

        // Set up the adapter for the ListView
        bookingAdapter = new BookingAdapter(this, bookingsList, confirmationRef);
        listViewBookings.setAdapter(bookingAdapter);

        // Retrieve bookings data from Firebase
        retrieveBookingsData();
    }

    private void retrieveBookingsData() {
        bookingsListener = confirmationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingsList.clear(); // Clear the previous data before adding new bookings

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ConfirmationData confirmationData = snapshot.getValue(ConfirmationData.class);
                    if (confirmationData != null) {
                        bookingsList.add(confirmationData);
                    }
                }
                // Update the adapter with the new data
                bookingAdapter.updateData(bookingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(BookingsActivity.this, "Failed to retrieve bookings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener when the activity is destroyed
        if (confirmationRef != null && bookingsListener != null) {
            confirmationRef.removeEventListener(bookingsListener);
        }
    }
}



