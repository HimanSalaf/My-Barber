package com.example.mybarber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimeSlot extends AppCompatActivity {
    private DatabaseReference slotsRef;
    private String selectedDate;
    private ArrayAdapter<String> dateAdapter;

    private TimeSlotAdapter slotAdapter;
    private RadioGroup slotRadioGroup;
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot);
        // Action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Booking Time");

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        slotsRef = database.getReference("Date");

        // Initialize UI elements
        Spinner dateSpinner = findViewById(R.id.date_spinner);
        slotRadioGroup = findViewById(R.id.slot_radio_group);
        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setEnabled(false); // Disable the Continue button initially

        // Initialize date spinner adapter
        List<String> dates = new ArrayList<>();
        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dates);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected date from the spinner
                selectedDate = dateSpinner.getSelectedItem().toString();

                // Get the selected time slot from the radio button
                int selectedRadioButtonId = slotRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String selectedTimeSlot = selectedRadioButton.getText().toString();

                // Save selectedDate and selectedTimeSlot values in SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedDate", selectedDate);
                editor.putString("selectedTimeSlot", selectedTimeSlot);
                editor.apply();

                // Mark the selected time slot as unavailable in the database
                DatabaseReference selectedTimeSlotRef = slotsRef.child(selectedDate).child(selectedTimeSlot);
                selectedTimeSlotRef.setValue("Unavailable");

                Intent intent = new Intent(TimeSlot.this, BookingBarber.class);
                startActivity(intent);
            }
        });

        // Add a listener to the RadioGroup for time slot selection
        slotRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Enable the Continue button if a time slot is selected
                continueBtn.setEnabled(checkedId != -1);
            }
        });
        // Listen for changes in date spinner selection
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDate = adapterView.getItemAtPosition(i).toString();

                // Retrieve time slots for selected date from Firebase Realtime Database
                slotsRef.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        slotRadioGroup.removeAllViews();

                        // Add radio buttons for each time slot with availability status
                        for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                            String slot = slotSnapshot.getKey();
                            String availability = slotSnapshot.getValue(String.class);

                            RadioButton radioButton = new RadioButton(TimeSlot.this);
                            radioButton.setText(slot);
                            radioButton.setEnabled(availability.equals(""));

                            // Add the radio button to the radio group
                            slotRadioGroup.addView(radioButton);
                        }
                        // Disable the Continue button initially when the time slots change
                        continueBtn.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Retrieve available dates from Firebase Realtime Database
        slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateAdapter.clear();

                // Add available dates to date spinner adapter
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    dateAdapter.add(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private class TimeSlotAdapter extends BaseAdapter {
        private Context context;
        private List<String> items;

        public TimeSlotAdapter(Context context, List<String> items) {
            this.context = context;
            this.items = items;
        }

        public void clear() {
            items.clear();
            notifyDataSetChanged();
        }

        public void add(String item) {
            items.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item_slot, parent, false);
            }

            String item = items.get(position);

            TextView timeSlotTextView = convertView.findViewById(R.id.time_slot_text);
            TextView availabilityTextView = convertView.findViewById(R.id.availability_text);

            timeSlotTextView.setText(item);
            availabilityTextView.setText(""); // You can set the availability text here if needed

            return convertView;
        }
    }
}




