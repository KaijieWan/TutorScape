package com.example.tutorscape.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.tutorscape.Model.TuitionCentre;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.example.tutorscape.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.referencing.GeodeticCalculator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapsSdkInitializedCallback {
    private static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers
    private MapView mapView;
    private GoogleMap myMap;
    private SearchView mapSearch;
    private Map<String, String> postalRadius;
    private String minPostalCode;
    private String maxPostalCode;

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
        postalRadius = new HashMap<>();

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Marker marker;
            @Override
            public boolean onQueryTextSubmit(String query) {
                postalRadius.clear();
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
                    /*String country = address.getCountryName();
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
                    }*/

                    double [] bounds = calculateBounds(address.getLatitude(), address.getLongitude(), 5);
                    Log.d("address.getLongitude", String.valueOf(address.getLongitude()));
                    double minLat = bounds[0];
                    double maxLat = bounds[1];
                    double minLon = bounds[2];
                    double maxLon = bounds[3];

                    // Convert the minimum latitude and longitude to postal code
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(minLat, minLon, 1, new Geocoder.GeocodeListener() {
                            @Override
                            public void onGeocode(@NonNull List<Address> minAddresses) {
                                if (minAddresses != null && !minAddresses.isEmpty()) {
                                    minPostalCode = minAddresses.get(0).getPostalCode();
                                    Log.d("minPostalCode", minPostalCode);
                                }
                            }
                        });
                    }

                    // Convert the maximum latitude and longitude to postal code
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Log.d("geocode max method", "called");
                        geocoder.getFromLocation(maxLat, maxLon, 1, new Geocoder.GeocodeListener() {
                            @Override
                            public void onGeocode(@NonNull List<Address> maxAddresses) {
                                if (maxAddresses != null && !maxAddresses.isEmpty()) {
                                    maxPostalCode = maxAddresses.get(0).getPostalCode();
                                    Log.d("maxPostalCode", maxPostalCode);
                                }
                            }
                        });
                    }
                    Log.d("minPostalCode", minPostalCode);
                    Log.d("maxPostalCode", maxPostalCode);
                    Query query_postal = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre")
                            .orderByChild("postal").startAt(minPostalCode).endAt(maxPostalCode);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    marker = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    query_postal.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TuitionCentre tuitionCentre = dataSnapshot.getValue(TuitionCentre.class);
                                Log.d("tuitionCentre Postal", tuitionCentre.getPostal());
                                postalRadius.put(tuitionCentre.getPostal(), tuitionCentre.getName());
                            }
                            Log.d("postalRadius", postalRadius.toString());
                            for(Map.Entry<String, String> entry : postalRadius.entrySet()){
                                try {
                                    List<Address> address_list = geocoder.getFromLocationName(entry.getKey(), 1);
                                    Address address1 = address_list.get(0);
                                    LatLng latLng1 = new LatLng(address1.getLatitude(), address1.getLongitude());
                                    marker = myMap.addMarker(new MarkerOptions().position(latLng1).title(entry.getValue()));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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