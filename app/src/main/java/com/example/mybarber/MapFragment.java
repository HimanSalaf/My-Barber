package com.example.mybarber;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment {
    private GoogleMap mMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Retrieve selectedCity value from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String selectedCity = preferences.getString("selectedCity", "");

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map,container,false);
        SupportMapFragment supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap=googleMap;
                updateMapForCity(selectedCity);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
        return view;
    }
    public void updateMapForCity(String selectedCity) {
        if (mMap != null) {
            // Clear any existing markers or overlays
            mMap.clear();
            // Load markers, overlays, or other features based on the selected city
            switch (selectedCity) {
                case "Kalmunai":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.418552441599798, 81.82323221491292), 12));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.418552441599798, 81.82323221491292)).title("14, Main Street, Kalmunai"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.412340349500557, 81.82934547733367)).title("145, Cassim Road, Kalmunai"));
                    break;
                case "Kattankudy":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.687333821980217, 81.72487861924759), 12));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.687333821980217, 81.72487861924759)).title("Main Street, Kattankudy"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.685521448613031, 81.72841232374844)).title("432, Telecom Rd, Kattankudy"));
                    break;
                case "Maruthamunai":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.431761542820204, 81.8172134568225), 12));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.431761542820204, 81.8172134568225)).title("Al Minan Rd, Pandiruppu"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.438150728822931, 81.81339919481611)).title("49C, Akbar Rd, Maruthamunai"));
                    break;
                case "Ampara":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.291199540059037, 81.67456034575785), 12));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.291199540059037, 81.67456034575785)).title("9Q7, 5th Ave, Ampara"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.30364827334832, 81.6708805084701)).title("B, 584 Dharmapala Mawatha, Ampara"));
                    break;
                case "Batticaloa":
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.720884646142966, 81.69638523081105), 12));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.73146538755847, 81.68616501948539)).title("19A Kumara Kovil Rd No 1, Batticaloa"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(7.720884646142966, 81.69638523081105)).title("Trincomalee Hwy, Batticaloa"));
                    break;
            }
        }
    }
}