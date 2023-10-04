package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Welcome");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        // Check if the user is already authenticated
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is already authenticated, retrieve and check their choice
            retrieveUserChoiceAndRedirect(user.getUid());
            return;
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        Button barberButton = findViewById(R.id.buttonLoginBarber);
        barberButton.setOnClickListener(view -> saveUserChoiceAndRedirect("Barber"));

        Button customerButton = findViewById(R.id.buttonLoginCustomer);
        customerButton.setOnClickListener(view -> saveUserChoiceAndRedirect("Customer"));
    }

    public void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void saveUserChoiceAndRedirect(String choice) {
        // Save the user's choice in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("choice", choice);
        editor.apply();

        // Redirect to the login activity
        openLogin();
    }

    private void retrieveUserChoiceAndRedirect(String userId) {
        DatabaseReference userChoiceRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("choice");

        userChoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String choice = dataSnapshot.getValue(String.class);

                if (choice != null && !choice.isEmpty()) {
                    // User has a choice saved in Firebase
                    // Redirect to the appropriate activity based on the choice
                    if (choice.equals("Customer")) {
                        startActivity(new Intent(WelcomeActivity.this, Home.class));
                    } else if (choice.equals("Barber")) {
                        startActivity(new Intent(WelcomeActivity.this, BarberHome.class));
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if needed
            }
        });
    }
}

