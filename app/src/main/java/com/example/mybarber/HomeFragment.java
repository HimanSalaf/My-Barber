package com.example.mybarber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {
    TextView nameTextView;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedUsername = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedUsername.getString("username", "");

        nameTextView = view.findViewById(R.id.user);
        nameTextView.setText(username);

        Button btnOpen = view.findViewById(R.id.btnNotification);
        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Notification.class);
            startActivity(intent);
        });

        // Initialize FirebaseStorage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Advertisements");

        // Find the image views in the layout
        ImageView imageView1 = view.findViewById(R.id.imageView1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);
        ImageView imageView4 = view.findViewById(R.id.imageView4);

        // Load images from Firebase Storage into the image views
        loadImageFromFirebaseStorage("newprice.png", imageView1);
        loadImageFromFirebaseStorage("stylist.png", imageView2);
        loadImageFromFirebaseStorage("booknowad.jpg", imageView3);
        loadImageFromFirebaseStorage("mobilead.png", imageView4);

        return view;
    }
    private void loadImageFromFirebaseStorage(String imageName, ImageView imageView) {
        if (isAdded()) {
            StorageReference imageRef = storageRef.child(imageName);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                if (isAdded()) {
                    // Load the image into the image view using a library like Picasso or Glide
                    Glide.with(requireContext())
                            .load(uri)
                            .into(imageView);
                }
            }).addOnFailureListener(exception -> {
                // Handle any errors that occurred during image loading
                Log.e("HomeFragment", "Error loading image: " + exception.getMessage());
            });
        }
    }
}
