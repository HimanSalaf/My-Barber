package com.example.mybarber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {
    private Spinner citySpinner;
    private List<String> cityList = new ArrayList<>();
    private String selectedCity = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_booking, container, false);

        Button btnOpen= view.findViewById(R.id.btnnoti1);
        btnOpen.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),Notification.class);
            startActivity(intent);
        });
        Button btnNext= view.findViewById(R.id.nxtBtn1);
        btnNext.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),BookingSaloon.class);
            intent.putExtra("selectedCity", selectedCity);
            startActivity(intent);
        });
        Button btnDoor=view.findViewById(R.id.doorBtn);
        btnDoor.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),DoorstepBooking.class);
            startActivity(intent);
        });
        Button btnViewBookings=view.findViewById(R.id.viewBookings);
        btnViewBookings.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),BookingsActivity.class);
            startActivity(intent);
        });
        // Initialize the Spinner
        citySpinner = view.findViewById(R.id.city_spinner);
        // Initialize the Firebase Realtime Database reference
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cities");
        // Set an event listener for the database reference
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the city list
                cityList.clear();
                // Loop through the child nodes and add the values to the city list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String city = snapshot.getKey();
                    cityList.add(city);
                }
                // Create an ArrayAdapter using the city list and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                citySpinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("BookingFragment", "Database error: " + databaseError.getMessage());
            }
        });
        // Set an event listener for the Spinner
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the item selection here
                selectedCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        return view;
    }
}