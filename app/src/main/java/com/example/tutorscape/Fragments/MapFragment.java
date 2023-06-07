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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.referencing.GeodeticCalculator;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapsSdkInitializedCallback {
    private static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers
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
                    String country = address.getCountryName();
                    String state = address.getAdminArea();
                    String city = address.getLocality();

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    marker = myMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    if(city != null && !city.isEmpty()){
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                    else if(state != null && !state.isEmpty()){
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                    else if(country != null && !country.isEmpty()){
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    else{
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    }

                    double [] bounds = calculateBounds(address.getLatitude(), address.getLongitude(), 10);
                    double minLat = bounds[0];
                    double maxLat = bounds[1];
                    double minLon = bounds[2];
                    double maxLon = bounds[3];

                    // Convert the minimum latitude and longitude to postal code
                    List<Address> minAddresses = null;
                    try {
                        minAddresses = geocoder.getFromLocation(minLat, minLon, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String minPostalCode = null;
                    if (minAddresses != null && !minAddresses.isEmpty()) {
                        minPostalCode = minAddresses.get(0).getPostalCode();
                    }

                    // Convert the maximum latitude and longitude to postal code
                    List<Address> maxAddresses = null;
                    try {
                        maxAddresses = geocoder.getFromLocation(maxLat, maxLon, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String maxPostalCode = null;
                    if (maxAddresses != null && !maxAddresses.isEmpty()) {
                        maxPostalCode = maxAddresses.get(0).getPostalCode();
                    }
                    Query query_levels = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre")
                            .orderByChild("postal").startAt(minPostalCode).endAt(maxPostalCode);
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

    public static double[] calculateBounds(double centerLat, double centerLon, double radius) {
        double latRadian = Math.toRadians(centerLat);

        // Calculate the latitude boundaries
        double deltaLat = radius / EARTH_RADIUS;
        double minLat = Math.toDegrees(latRadian - deltaLat);
        double maxLat = Math.toDegrees(latRadian + deltaLat);

        // Calculate the longitude boundaries
        double deltaLon = radius / (EARTH_RADIUS * Math.cos(latRadian));
        double minLon = Math.toDegrees(centerLon - deltaLon);
        double maxLon = Math.toDegrees(centerLon + deltaLon);

        return new double[] { minLat, maxLat, minLon, maxLon };
    }
}