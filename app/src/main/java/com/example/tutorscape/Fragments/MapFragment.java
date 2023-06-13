package com.example.tutorscape.Fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tutorscape.Adapter.CustomInfoWindowAdapter;
import com.example.tutorscape.Adapter.NothingSelectedSpinnerAdapter;
import com.example.tutorscape.Model.TuitionCentre;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.example.tutorscape.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.referencing.GeodeticCalculator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapsSdkInitializedCallback, GoogleMap.OnMarkerClickListener {
    private static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers
    private MapView mapView;
    private GoogleMap myMap;
    private SearchView mapSearch;
    private Marker markerFixed;
    private CustomInfoWindowAdapter customInfoWindowAdapter;
    private Spinner spinnerLevel;
    private Spinner spinnerExam;
    private Spinner spinnerSubject;
    private Spinner spinnerType;

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
        customInfoWindowAdapter = new CustomInfoWindowAdapter(LayoutInflater.from(getContext()), getContext());

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
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinnerLevel = view.findViewById(R.id.filter_1);

        /*String[] spinnerLevelOptions = new String[options.length+1];
        spinnerLevelOptions[0] = "Level";
        System.arraycopy(options, 0 , spinnerLevelOptions,1, options.length);*/

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(getContext(), R.layout.map_filter_items, getResources().getStringArray(R.array.level_selection));
        spinnerLevel.setPrompt("Level");
        spinnerLevel.setAdapter(
                new NothingSelectedSpinnerAdapter(levelAdapter, R.layout.contact_spinner_row_nothing_selected, getContext())
        );
        //spinnerLevel.setSelection(0, false);

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (position != 0) {
                    // Do something with the selected item
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });
        spinnerExam = view.findViewById(R.id.filter_2);
        spinnerSubject = view.findViewById(R.id.filter_3);
        spinnerType = view.findViewById(R.id.filter_4);

        return view;
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        //println(it.name)
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                myMap = googleMap;
                myMap.setOnMarkerClickListener(MapFragment.this);

                LatLng singapore = new LatLng(1.290270, 103.851959);
                //myMap.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 10));

                DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre");
                Geocoder geocoder = new Geocoder(getContext());
                MarkerOptions markerOptions = new MarkerOptions();
                Drawable defaultMarkerDrawable = getResources().getDrawable(R.drawable.ic_map_marker_blue, getContext().getTheme());
                Drawable blueMarkerDrawable = defaultMarkerDrawable.mutate();
                blueMarkerDrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(drawableToBitmap(blueMarkerDrawable), 95, 95, false));
                markerOptions.icon(markerIcon);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List <Address> addressList1 = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            TuitionCentre tuitionCentre = snapshot.getValue(TuitionCentre.class);
                            try {
                                addressList1 = geocoder.getFromLocationName(tuitionCentre.getPostal(), 1);
                                Address address1 = addressList1.get(0);
                                LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());
                                markerFixed = myMap.addMarker(markerOptions.position(latLng));

                                markerFixed.setTag(tuitionCentre);
                                myMap.setInfoWindowAdapter(customInfoWindowAdapter);
                                //myMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getContext()), getContext()));
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
        });
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    /*private void showCustomInfoWindow(LatLng position, String title) {
        // Inflate the custom layout for the info window
        View customInfoWindowView = getLayoutInflater().inflate(R.layout.custom_info_window_layout, null);

        // Customize the contents of the custom info window
        TextView titleTextView = customInfoWindowView.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        // Create a new info window and set the custom view
        InfoWindow infoWindow = new InfoWindow(customInfoWindowView, position, -47, -10);

        // Show the info window on the map
        infoWindow.open(googleMap);
    }*/

                    /*double [] bounds = calculateBounds(address.getLatitude(), address.getLongitude(), 5);
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
    }*/
}