package com.example.mybarber;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoorstepBooking extends AppCompatActivity {
    private TextView date;
    private TextView time;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorstep_booking);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Doorstep Booking");

        date = findViewById(R.id.showDate);
        time = findViewById(R.id.showTime);
        Button button = findViewById(R.id.dateBtn);
        Button button1 = findViewById(R.id.timeBtn);
        btnNext = findViewById(R.id.nxtBtn);
        btnNext.setEnabled(false);
        Button doorstep = findViewById(R.id.doorstepApp);
        doorstep.setOnClickListener(view -> {
            Intent intent = new Intent(DoorstepBooking.this, DoorstepAppointments.class);
            startActivity(intent);
        });

        button.setOnClickListener(view -> openDatePicker());
        button1.setOnClickListener(view -> openTimePicker());

        btnNext.setOnClickListener(view -> {
            saveAppointment();
            Intent intent = new Intent(DoorstepBooking.this, DoorstepBarber.class);
            startActivity(intent);
        });
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            LocalDate selectedDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                selectedDate = LocalDate.of(year, month, day);
            }
            assert selectedDate != null;
            date.setText(selectedDate.toString());
            checkNextButtonEnabled();
        }, 2023, 6, 20);
        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            LocalTime selectedTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                selectedTime = LocalTime.of(hour, minute);
            }
            assert selectedTime != null;
            time.setText(selectedTime.toString());
            checkNextButtonEnabled();
        }, 0, 0, true);
        timePickerDialog.show();
    }

    private void checkNextButtonEnabled() {
        if (!date.getText().toString().isEmpty() && !time.getText().toString().isEmpty()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    private void saveAppointment() {
        String selectedDate = date.getText().toString();
        String selectedTime = time.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedDoorstepDate", selectedDate);
        editor.putString("selectedDoorstepTime", selectedTime);
        editor.apply();
    }
}


