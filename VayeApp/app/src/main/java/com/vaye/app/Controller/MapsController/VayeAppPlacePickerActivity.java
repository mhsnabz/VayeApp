package com.vaye.app.Controller.MapsController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;
import com.vaye.app.R;

import java.util.Arrays;
import java.util.Locale;

public class VayeAppPlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback , LocationListener {
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = "PlacePicker";
    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    AutocompleteSupportFragment autocompleteFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app_place_picker);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.map_api_key), Locale.getDefault());
            placesClient = Places.createClient(this);
            initMap();
        }



    }

    public void goMyLocation(View view) {
        getDevicesCurrentLocation();
    }

    public void search(View view) {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(VayeAppPlacePickerActivity.this,"Harita Hazır",Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        getDevicesCurrentLocation();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));

        // Use the builder to create a FindAutocompletePredictionsRequest.



    }
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


            autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS_COMPONENTS));
           /* ;*/
            autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
            autocompleteFragment.setCountries("TR");
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NotNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + " " + place.getAddress());
                }


                @Override
                public void onError(@NotNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status.getStatusMessage());
                }
            });


    }
    private void getDevicesCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(VayeAppPlacePickerActivity.this);
        try {
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()){
                        Location currentLocation = task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"Konumunuz");
                        setBounds(task.getResult(),5500);
                    }else{
                        Log.d(TAG, "onComplete: "+"current location is null");
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (SecurityException e){
            Log.d(TAG, "getDevicesCurrentLocation: "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String locationName) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(locationName);
        mMap.addMarker(options);
    }
    private void setBounds(Location location, int mDistanceInMeters ){
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong)));
        /*autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong)));*/
        Log.d(TAG,"Min: "+Double.toString(minLat)+","+Double.toString(minLong));
        Log.d(TAG,"Max: "+Double.toString(maxLat)+","+Double.toString(maxLong));

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        setBounds(location,5500);
    }
}