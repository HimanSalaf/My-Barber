package com.example.mybarber;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoorstepBarber extends AppCompatActivity {
    private Spinner barberSpinner;
    private EditText addressEditText;
    private List<String> barberList = new ArrayList<>();
    private String selectedBarber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorstep_barber);

        addressEditText = findViewById(R.id.addressEditText);

        Button continueToBtn = findViewById(R.id.continueToBtn);
        continueToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected barber and address
                String address = addressEditText.getText().toString();
                selectedBarber = barberSpinner.getSelectedItem().toString();
                if (address.trim().isEmpty()) {
                    Toast.makeText(DoorstepBarber.this, "Enter Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Save the selected barber and address in SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedDoorstepBarber", selectedBarber);
                editor.putString("DoorstepAddress", address);
                editor.apply();

                Intent intent=new Intent(DoorstepBarber.this,DoorstepConfirmation.class);
                startActivity(intent);
            }
        });

        if(isLocationPermissionGranted()){
        Fragment fragment=new GpsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.gpsLayout,fragment).commit();
        }
        else {
            requestLocationPermission();
        }
        barberSpinner = findViewById(R.id.bSpinner);
        // Initialize the Firebase Realtime Database reference
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("barbers");
        // Set an event listener for the database reference
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the city list
                barberList.clear();
                // Loop through the child nodes and add the values to the city list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String barber = snapshot.getKey();
                    barberList.add(barber);
                }
                // Create an ArrayAdapter using the city list and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DoorstepBarber.this, android.R.layout.simple_spinner_item, barberList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                barberSpinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Set an event listener for the Spinner
        barberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBarber = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Method to set the address text
    void setAddressText(String address) {
        addressEditText.setText(address);
    }
    //check the location permission granted or not
    private boolean isLocationPermissionGranted(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            return true;}
        else {
            return false;}
    }
    //request location permission
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
    }
    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle the logic here
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}