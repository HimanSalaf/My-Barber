package com.example.mybarber;

import android.os.Bundle;
import android.widget.ListView;

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

public class Notification extends AppCompatActivity {

    private ListView notificationListView;
    private DatabaseReference notificationsRef;
    private ValueEventListener notificationsListener;
    private List<NotificationDetails> notificationList;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Notifications");

        notificationListView = findViewById(R.id.notification_list);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not signed in
            return;
        }
        String userId = currentUser.getUid();

        // Get a reference to the notifications for the current user
        notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        // Set up a listener to fetch the notifications
        notificationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationDetails notificationDetails = snapshot.getValue(NotificationDetails.class);
                    if (notificationDetails != null) {
                        notificationList.add(notificationDetails);
                    }
                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening for notifications
        notificationsRef.addValueEventListener(notificationsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening for notifications
        notificationsRef.removeEventListener(notificationsListener);
    }
}
