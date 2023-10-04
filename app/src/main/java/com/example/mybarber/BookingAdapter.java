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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class BookingAdapter extends ArrayAdapter<ConfirmationData> {

    private Context context;
    private List<ConfirmationData> bookingsList;
    private DatabaseReference confirmationRef;

    public BookingAdapter(Context context, List<ConfirmationData> bookingsList, DatabaseReference confirmationRef) {
        super(context, 0, bookingsList);
        this.context = context;
        this.bookingsList = bookingsList;
        this.confirmationRef = confirmationRef;
    }

    public void updateData(List<ConfirmationData> bookingsList) {
        this.bookingsList = bookingsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current booking item
        ConfirmationData booking = bookingsList.get(position);

        // Set the data to the ViewHolder views
        viewHolder.textViewBookingId.setText("Booking ID: " + booking.getBookingId());
        viewHolder.textViewName.setText("Name: " + booking.getTextViewName());
        viewHolder.textViewMobile.setText("Mobile: " + booking.getTextViewPhone());
        viewHolder.textViewDate.setText("Date: " + booking.getTextViewDate());
        viewHolder.textViewTimeSlot.setText("Time Slot: " + booking.getTextViewTimeSlot());
        viewHolder.textViewBarber.setText("Barber: " + booking.getTextViewBarber());
        viewHolder.textViewAddress.setText("Address: " + booking.getTextViewAddress());
        viewHolder.textViewServices.setText("Services: " + booking.getTextViewServices());
        viewHolder.textViewTotal.setText("Total: " + booking.getTextViewTotal() + "0");

        // Cancel booking button click listener
        viewHolder.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Cancellation");
                builder.setMessage("Are you sure you want to cancel this booking?");

                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button, proceed with cancellation
                        String bookingId = booking.getBookingId();
                        DatabaseReference bookingRef = BookingAdapter.this.confirmationRef.child(bookingId);

                        bookingRef.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                // Retrieve the booking data from the database
                                ConfirmationData bookingData = mutableData.getValue(ConfirmationData.class);

                                if (bookingData == null) {
                                    // Booking data does not exist, return an error
                                    return Transaction.abort();
                                }
                                // Perform cancellation logic
                                // For example, set a "cancelled" flag to true or update relevant fields
                                bookingData.setCancelled(true);
                                // Set the updated booking data back to the database
                                mutableData.setValue(bookingData);
                                // Indicate success to the transaction
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                if (committed) {
                                    // Cancellation successful
                                    Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();

                                    // Remove the booking from the list and notify the adapter
                                    bookingsList.remove(position);
                                    notifyDataSetChanged();

                                    // Delete the booking data from the database
                                    DatabaseReference bookingDataRef = dataSnapshot.getRef();
                                    bookingDataRef.removeValue();
                                } else {
                                    // Cancellation failed
                                    String errorMessage = databaseError != null ? databaseError.getMessage() : "Unknown error";
                                    Toast.makeText(context, "Failed to cancel booking: " + errorMessage, Toast.LENGTH_SHORT).show();
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
                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewBookingId;
        TextView textViewName;
        TextView textViewMobile;
        TextView textViewDate;
        TextView textViewTimeSlot;
        TextView textViewBarber;
        TextView textViewAddress;
        TextView textViewServices;
        TextView textViewTotal;
        Button btnCancelBooking;

        ViewHolder(View view) {
            textViewBookingId = view.findViewById(R.id.textViewBookingId);
            textViewName = view.findViewById(R.id.textViewName);
            textViewMobile = view.findViewById(R.id.textViewPhone);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewTimeSlot = view.findViewById(R.id.textViewTimeSlot);
            textViewBarber = view.findViewById(R.id.textViewBarber);
            textViewAddress = view.findViewById(R.id.textViewAddress);
            textViewServices = view.findViewById(R.id.textViewServices);
            textViewTotal = view.findViewById(R.id.textViewTotal);
            btnCancelBooking = view.findViewById(R.id.btnCancelBooking);
        }
    }
}



