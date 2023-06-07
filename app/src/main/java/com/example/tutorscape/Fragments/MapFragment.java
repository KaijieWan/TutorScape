package com.example.tutorscape.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.example.tutorscape.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapsSdkInitializedCallback {
    private MapView mapView;
    private GoogleMap myMap;
    private SearchView mapSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MapFragment", "onCreateView called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if(view == null) {
            Log.e("MapFragment", "Failed to inflate layout");
        }
        mapView = view.findViewById(R.id.mapView);
        mapSearch = view.findViewById(R.id.mapSearch);
        MapsInitializer.initialize(getContext(), MapsInitializer.Renderer.LATEST, this);
        mapView.onCreate(savedInstanceState);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Marker marker;
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(marker!=null){
                    marker.remove();
                }
                String location = mapSearch.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    marker = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        //println(it.name)
        Log.d("TAG", "onMapsSdkInitialized: ");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d("OnMapReady", "called");
                myMap = googleMap;

                LatLng singapore = new LatLng(1.290270, 103.851959);
                //myMap.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 10));
            }
        });
    }
}