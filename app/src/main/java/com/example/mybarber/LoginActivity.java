package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private Button otpbutton;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        final EditText username = findViewById(R.id.name);
        final EditText phone = findViewById(R.id.phone);
        otpbutton = findViewById(R.id.otpbutton);

        final ProgressBar progressBar = findViewById(R.id.progressbar);

        otpbutton.setOnClickListener(view -> {
            if (phone.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Mobile", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter User Name", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            otpbutton.setVisibility(View.INVISIBLE);
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber("+94" + phone.getText().toString())
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(LoginActivity.this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            progressBar.setVisibility(View.GONE);
                            otpbutton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            progressBar.setVisibility(View.GONE);
                            otpbutton.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                            progressBar.setVisibility(View.GONE);
                            otpbutton.setVisibility(View.VISIBLE);

                            // Save the username entered by the user to SharedPreferences
                            SharedPreferences sharedUsername  = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedUsername.edit();
                            editor.putString("username", username.getText().toString());
                            editor.putString("phone", "+94"+phone.getText().toString());
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), GetOtp.class);
                            intent.putExtra("mobile", phone.getText().toString());
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);
                        }
                            })
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }
}
