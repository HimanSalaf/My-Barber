package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class GetOtp extends AppCompatActivity {
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getotp);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("OTP Verification");

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        final EditText otp = findViewById(R.id.etOtp);
        final ProgressBar progressBar = findViewById(R.id.progressbar);
        final Button buttonVerify = findViewById(R.id.btnVerifyOtp);
        TextView tvResendOtp = findViewById(R.id.tvResendOtp);

        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the phone number and start the verification process again
                String phoneNumber = getIntent().getStringExtra("mobile");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+94" + phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        GetOtp.this,
                        mCallbacks
                );
            }
        });

        verificationId = getIntent().getStringExtra("verificationId");
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().trim().isEmpty()) {
                    Toast.makeText(GetOtp.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp.getText().toString();

                if (verificationId != null) {
                    progressBar.setVisibility(view.VISIBLE);
                    buttonVerify.setVisibility(view.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(view.GONE);
                                    buttonVerify.setVisibility(view.VISIBLE);
                                    if (task.isSuccessful()) {
                                        // User has successfully authenticated
                                        // Save the user's choice in Firebase
                                        saveUserChoiceAndRedirect();
                                    } else {
                                        Toast.makeText(GetOtp.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void saveUserChoiceAndRedirect() {
        String choice = sharedPreferences.getString("choice", ""); // Retrieve user's choice

        if (!choice.isEmpty()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                // Create a Firebase reference to store the user's choice
                DatabaseReference userChoiceRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("choice");

                // Set the user's choice in the database
                userChoiceRef.setValue(choice)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Choice saved successfully
                                // Redirect to the appropriate activity based on the choice
                                if (choice.equals("Customer")) {
                                    Intent intent = new Intent(GetOtp.this, Home.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else if (choice.equals("Barber")) {
                                    Intent intent = new Intent(GetOtp.this, BarberHome.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error, if any
                                Toast.makeText(GetOtp.this, "Failed to save choice", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
