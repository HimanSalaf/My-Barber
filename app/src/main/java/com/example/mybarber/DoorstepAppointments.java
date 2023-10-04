package com.example.mybarber;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
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

public class DoorstepAppointments extends AppCompatActivity {
    private AppointmentAdapter adapter;
    private List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorstep_appointments);

        ListView listView = findViewById(R.id.doorteplistViewBookings);
        appointments = new ArrayList<>();
        adapter = new AppointmentAdapter(getApplicationContext(), appointments);
        listView.setAdapter(adapter);

        // Retrieve the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the bookings for the current user from Firebase
            DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("DoorStep Confirmations").child(userId);
            appointmentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    appointments.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = snapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            appointments.add(appointment);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }
}


