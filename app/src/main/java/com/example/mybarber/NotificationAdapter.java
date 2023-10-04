package com.example.mybarber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationDetails> {

    public NotificationAdapter(Context context, List<NotificationDetails> notificationList) {
        super(context, 0, notificationList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        NotificationDetails notification = getItem(position);
        if (notification != null) {
            TextView titleTextView = convertView.findViewById(R.id.notification_title);
            TextView messageTextView = convertView.findViewById(R.id.notification_message);

            titleTextView.setText(notification.getTitle());
            messageTextView.setText(notification.getMessage());
        }

        return convertView;
    }
}

