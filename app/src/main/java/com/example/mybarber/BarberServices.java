package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BarberServices extends AppCompatActivity {
    private Button btnConfirm;
    private List<BarberService> services;
    private BarberServiceAdapter adapter;
    private List<BarberService> selectedServices;
    private SharedPreferences sharedPreferences;
    private static final String SELECTED_SERVICES_PREF = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_services);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Barber Services");

        // Initialize services data
        services = new ArrayList<>();
        services.add(new BarberService("Hair Cut", R.drawable.haircut, 300.00));
        services.add(new BarberService("Beard Shave", R.drawable.trimmingbeard, 250.00));
        services.add(new BarberService("Hair Colouring", R.drawable.haircolouring, 300.00));
        services.add(new BarberService("Hair Styling", R.drawable.hairstyle, 400.00));
        services.add(new BarberService("Baby Haircut", R.drawable.babycut, 200.00));
        services.add(new BarberService("Children Haircut", R.drawable.childrencut, 250.00));
        services.add(new BarberService("Facial", R.drawable.facial, 400.00));

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.servicesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BarberServiceAdapter(services);
        recyclerView.setAdapter(adapter);

        // Initialize selected services list
        selectedServices = new ArrayList<>();

        // Set item click listener for RecyclerView
        adapter.setOnItemClickListener(new BarberServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BarberService service = services.get(position);
                if (selectedServices.contains(service)) {
                    selectedServices.remove(service);
                } else {
                    selectedServices.add(service);
                }
                adapter.notifyItemChanged(position); // Update item view
                updateConfirmButtonState();
            }
        });

        // Continue button click listener
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if at least one service is selected
                if (selectedServices.isEmpty()) {
                    Toast.makeText(BarberServices.this, "Please select at least one service", Toast.LENGTH_SHORT).show();
                } else {
                    // Here, we simply display the names of the selected services in a Toast
                    StringBuilder selectedServicesText = new StringBuilder();
                    for (BarberService service : selectedServices) {
                        selectedServicesText.append(service.getName()).append("\n");
                    }
                    double totalPrice = calculateTotalPrice();
                    saveSelectedServices(selectedServicesText.toString(), totalPrice);
                    Intent intent = new Intent(BarberServices.this, ConfirmationActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Disable the confirm button initially
        btnConfirm.setEnabled(false);

        // Obtain the SharedPreferences instance
        sharedPreferences = getSharedPreferences(SELECTED_SERVICES_PREF, MODE_PRIVATE);
    }

    private void updateConfirmButtonState() {
        // Enable or disable the confirm button based on the selected services
        btnConfirm.setEnabled(!selectedServices.isEmpty());
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.00;
        for (BarberService service : selectedServices) {
            totalPrice += service.getPrice();
        }
        return totalPrice;
    }

    private void saveSelectedServices(String selectedServicesText, double totalPrice) {
        // Save the selected services text and total price to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedServicesText", selectedServicesText);
        editor.putString("totalPrice", String.format("%.2f", totalPrice));
        editor.apply();
    }
}




