package com.example.mybarber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {
    private List<Appointment> appointments;
    private DatabaseReference confirmationRef;

    public AppointmentAdapter(Context context, List<Appointment> appointments) {
        super(context, 0, appointments);
        this.appointments = appointments;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            confirmationRef = FirebaseDatabase.getInstance().getReference("DoorStep Confirmations").child(userId);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_appointment, parent, false);
        }

        Appointment appointment = getItem(position);

        TextView bookingIdTextView = convertView.findViewById(R.id.bookingIdTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView barberTextView = convertView.findViewById(R.id.barberTextView);
        TextView addressTextView = convertView.findViewById(R.id.addressTextView);

        if (appointment != null) {
            bookingIdTextView.setText("Booking ID: " + appointment.getBookingId());
            dateTextView.setText("Appointment Date: " + appointment.getDate());
            timeTextView.setText("Appointment Time: " + appointment.getTime());
            barberTextView.setText("Barber: " + appointment.getBarber());
            addressTextView.setText("Address: " + appointment.getAddress());

            Button cancelButton = convertView.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    builder.setTitle("Confirm Cancellation");
                    builder.setMessage("Are you sure you want to cancel this booking?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String bookingId = appointment.getBookingId();
                            DatabaseReference bookingRef = confirmationRef.child(bookingId);

                            bookingRef.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    ConfirmationData bookingData = mutableData.getValue(ConfirmationData.class);

                                    if (bookingData == null) {
                                        return Transaction.abort();
                                    }

                                    // Perform cancellation logic
                                    bookingData.setCancelled(true);

                                    // Set the updated booking data back to the database
                                    mutableData.setValue(bookingData);

                                    // Indicate success to the transaction
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                    if (committed) {
                                        Toast.makeText(parent.getContext(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show();

                                        // Remove the booking from the list and notify the adapter
                                        appointments.remove(appointment);
                                        notifyDataSetChanged();

                                        // Delete the booking data from the database
                                        if (dataSnapshot != null)
                                            dataSnapshot.getRef().removeValue();
                                    } else {
                                        String errorMessage = databaseError != null ? databaseError.getMessage() : "Unknown error";
                                        Toast.makeText(parent.getContext(), "Failed to cancel booking: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        Log.e("CancellationError", errorMessage);
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked No button, do nothing
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        return convertView;
    }
}




