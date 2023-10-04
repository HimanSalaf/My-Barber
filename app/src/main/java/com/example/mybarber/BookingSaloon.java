package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BookingSaloon extends AppCompatActivity {
    private int addressArrayResourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_saloon);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button btnNext3 = findViewById(R.id.nxtBtn3);
        btnNext3.setOnClickListener(view -> {
            Intent intent = new Intent(BookingSaloon.this, TimeSlot.class);
            startActivity(intent);
        });

        Button button = findViewById(R.id.btnnoti1);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Notification.class);
            startActivity(intent);
        });

        String selectedCity = getIntent().getStringExtra("selectedCity");
        if ("Kalmunai".equals(selectedCity)) {
            addressArrayResourceId = R.array.kalmunai_address;
        } else if ("Maruthamunai".equals(selectedCity)) {
            addressArrayResourceId = R.array.maruthamunai_address;
        } else if ("Kattankudy".equals(selectedCity)) {
            addressArrayResourceId = R.array.kattankudy_address;
        } else if ("Ampara".equals(selectedCity)) {
            addressArrayResourceId = R.array.ampara_address;
        } else if ("Batticaloa".equals(selectedCity)) {
            addressArrayResourceId = R.array.batticaloa_address;
        }

        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapLayout, fragment).commit();

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedCity", selectedCity);
        editor.apply();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapFragment mapFragment = new MapFragment();
        mapFragment.updateMapForCity(selectedCity);
        fragmentTransaction.replace(R.id.mapLayout, mapFragment);
        fragmentTransaction.commit();

        Spinner addSpinner = findViewById(R.id.add_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, addressArrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addSpinner.setAdapter(adapter);

        addSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAddress = parent.getItemAtPosition(position).toString();
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedAddress", selectedAddress);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}
