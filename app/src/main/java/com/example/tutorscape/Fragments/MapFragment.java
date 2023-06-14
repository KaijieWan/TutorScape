package com.example.tutorscape.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.tutorscape.Adapter.CustomInfoWindowAdapter;
import com.example.tutorscape.Adapter.NothingSelectedSpinnerAdapter;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.ResultsActivity;
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
import com.google.firebase.database.ValueEventListener;

//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.referencing.GeodeticCalculator;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapsSdkInitializedCallback, GoogleMap.OnMarkerClickListener
    ,GoogleMap.OnInfoWindowClickListener{
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
    private List<Marker> allMarkers;
    private List<String> filters;
    private Button applyButton;
    private Button resetButton;

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

        applyButton = view.findViewById(R.id.apply_button);
        resetButton = view.findViewById(R.id.reset_button);

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
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(getContext(), R.layout.map_filter_items, getResources().getStringArray(R.array.level_selection));
        spinnerLevel.setPrompt("Level");
        spinnerLevel.setAdapter(
                new NothingSelectedSpinnerAdapter(levelAdapter, R.layout.level_spinner_row_nothing_selected, getContext())
        );

        spinnerExam = view.findViewById(R.id.filter_2);
        ArrayAdapter<String> examAdapter = new ArrayAdapter<String>(getContext(), R.layout.map_filter_items, getResources().getStringArray(R.array.exam_selection));
        spinnerExam.setPrompt("Exam");
        spinnerExam.setAdapter(
                new NothingSelectedSpinnerAdapter(examAdapter, R.layout.exam_spinner_row_nothing_selected, getContext())
        );

        spinnerSubject = view.findViewById(R.id.filter_3);
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getContext(), R.layout.map_filter_items, getResources().getStringArray(R.array.subject_selection));
        spinnerSubject.setPrompt("Exam");
        spinnerSubject.setAdapter(
                new NothingSelectedSpinnerAdapter(subjectAdapter, R.layout.subject_spinner_row_nothing_selected, getContext())
        );

        spinnerType = view.findViewById(R.id.filter_4);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), R.layout.map_filter_items, getResources().getStringArray(R.array.type_selection));
        spinnerType.setPrompt("Exam");
        spinnerType.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter, R.layout.type_spinner_row_nothing_selected, getContext())
        );

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }

            private void applyFilters() {
                String selectedLevel = spinnerLevel.getSelectedItem().toString();
                String selectedExam = spinnerExam.getSelectedItem().toString();
                String selectedSubject = spinnerSubject.getSelectedItem().toString();
                String selectedType = spinnerType.getSelectedItem().toString();

                for(Marker marker : allMarkers){
                    //getTag for each marker for each attribute to be filtered through
                }
            }


        });

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
                                markerFixed = myMap.addMarker(markerOptions.position(latLng).snippet(tuitionCentre.getId()));

                                markerFixed.setTag(tuitionCentre);
                                allMarkers.add(markerFixed);
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

                myMap.setOnInfoWindowClickListener(MapFragment.this);
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        String tuitionCentreId = marker.getSnippet();
        Intent intent = new Intent(getContext(), ResultsActivity.class);
        intent.putExtra("tuitionCentreId", tuitionCentreId);
        // Redirect to another page or view based on the selected item
        getContext().startActivity(intent);
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