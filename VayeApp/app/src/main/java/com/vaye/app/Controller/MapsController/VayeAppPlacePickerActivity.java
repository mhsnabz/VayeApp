package com.vaye.app.Controller.MapsController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.rpc.Help;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Application.VayeApp;
import com.vaye.app.Interfaces.LocationCallback;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VayeAppPlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback , LocationListener   {
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = "PlacePicker";
    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    AutocompleteSupportFragment autocompleteFragment;
    Double lat ,longLat;
    String locaitonName;
    Marker prevMarker;
    Location currentLocation ;
    LocationCallback callback;
    private FusedLocationProviderClient locationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app_place_picker);

        initMap();


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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);



        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));

       googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

           @Override
           public boolean onMarkerClick(Marker marker) {

               lat= marker.getPosition().latitude;
               longLat=marker.getPosition().longitude;
              locaitonName = marker.getTitle();

               prevMarker = marker;
               return false;
           }

       });

       googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
           @Override
           public void onMapClick(LatLng latLng) {
               if (prevMarker != null) {
                   prevMarker.remove();
               }


               prevMarker = googleMap.addMarker(new MarkerOptions()
                       .position(latLng)
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                       .snippet("Bu Konumu Seç")
                       .title(getCompleteAddressString(latLng.latitude,latLng.longitude)));


           }
       });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                    WaitDialog.show(VayeAppPlacePickerActivity.this,null);
                Helper.shared().LocationPickDialog(VayeAppPlacePickerActivity.this, arg0.getTitle(), getCompleteAddressString(arg0.getPosition().latitude,arg0.getPosition().longitude),arg0.getPosition().latitude, arg0.getPosition().longitude,callback, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {

                    }
                });
                    WaitDialog.dismiss();

            }
        });

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {

                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Canont get Address!");
        }
        return strAdd;
    }
    private void initMap(){

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(VayeAppPlacePickerActivity.this);
        Places.initialize(VayeAppPlacePickerActivity.this, getApplicationContext().getString(R.string.map_api_key));
        placesClient = Places.createClient(this);
            autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
           /* ;*/
            autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
            autocompleteFragment.setCountries("TR");
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NotNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + " " + place.getLatLng());
                  //  geoLocate(place.getLatLng(),place.getName(),place.getName());
                    moveCamera(place.getLatLng(),DEFAULT_ZOOM,place.getName());
                    lat = place.getLatLng().latitude;
                    longLat = place.getLatLng().latitude;
                    locaitonName = place.getName();
                    if (prevMarker!=null){
                        prevMarker.remove();
                    }
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
                         currentLocation = task.getResult();
                         if (currentLocation!=null){
                             moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"Konumunuz");
                             setBounds(task.getResult(),5500);
                         }

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
    private void geoLocate(String searchString,String title) {
        Geocoder geocoder = new Geocoder(VayeAppPlacePickerActivity.this);
        List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString,1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: "+ e.getMessage() );
            }
            if (list.size() > 0 ){
                Address address = list.get(0);

                Log.d(TAG, "geoLocate: found location" + address.toString());
                if (title!=null && !title.isEmpty()){
                    moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,title);
                }else{
                    moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
                }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(VayeAppPlacePickerActivity.this);
    }


}