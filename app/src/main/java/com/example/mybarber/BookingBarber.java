package com.example.mybarber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingBarber extends AppCompatActivity {

    Button btnConfirm;
    boolean isBarberSelected = false;
    private ArrayList<DataClass> dataList;
    private BarberAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_barber);

        GridView gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new BarberAdapter(this, dataList);
        gridView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setEnabled(false); // Disable the button initially

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBarberName = dataList.get(position).getCaption();
            saveSelectedBarberName(selectedBarberName);
            isBarberSelected = true;
            btnConfirm.setEnabled(true); // Enable the button when a barber is selected
            Toast.makeText(BookingBarber.this, "You have selected " + selectedBarberName, Toast.LENGTH_SHORT).show();
        });

        btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(BookingBarber.this, BarberServices.class);
            startActivity(intent);
        });
    }

    private void saveSelectedBarberName(String barberName) {
        // Save selected barber name in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedBarberName", barberName);
        editor.apply();
    }
}






