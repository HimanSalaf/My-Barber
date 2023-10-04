package com.example.mybarber;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText mSearchText;
    private LatLng mSelectedLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder mGeocoder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gps, container, false);

        mSearchText = view.findViewById(R.id.search_text);
        ImageButton mSearchButton = view.findViewById(R.id.search_button);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mGeocoder = new Geocoder(requireContext(), Locale.getDefault());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.gps);
        mapFragment.getMapAsync(this);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = mSearchText.getText().toString();
                searchLocation(searchString);
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a long click listener to the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Create a marker at the clicked location
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Selected Location");

                // Clear any existing markers and add the new marker to the map
                mMap.clear();
                mMap.addMarker(markerOptions);
                // Update the selected location
                mSelectedLocation = latLng;

                // Get the address for the selected location
                getAddressFromLocation(latLng);
            }
        });

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Get the user's current location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                // Move the camera to the user's current location
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15), 2000, null);
                                // Get the address for the current location
                                getAddressFromLocation(currentLatLng);
                            }
                        }
                    });
        } else {
            // Request permission to access the user's location
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // If a location has already been selected, add a marker for it
        if (mSelectedLocation != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mSelectedLocation);
            markerOptions.title("Selected Location");
            mMap.addMarker(markerOptions);
        }
    }

    private void searchLocation(String searchString) {
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 2000, null);

            // Get the address for the searched location
            getAddressFromLocation(latLng);
        }
    }

    private void getAddressFromLocation(LatLng latLng) {
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            StringBuilder addressBuilder = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressBuilder.append(address.getAddressLine(i));
                if (i < address.getMaxAddressLineIndex()) {
                    addressBuilder.append(", ");
                }
            }
            String fullAddress = addressBuilder.toString();

            // Update the address in the DoorstepBarber activity
            DoorstepBarber activity = (DoorstepBarber) requireActivity();
            activity.setAddressText(fullAddress);
        }
    }
}



