package com.company.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                centerMapOnLocation(lastKnownLocation);
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle bundle = getIntent().getExtras();

        double lat = bundle.getDouble("latitude");
        double lon = bundle.getDouble("longitude");
        int index = bundle.getInt("index", 0);

        if(bundle.getInt("index", 0) == 0) {


            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location curlocation) {
                    centerMapOnLocation(curlocation);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                centerMapOnLocation(lastKnownLocation);
            }

        }
        else {
            Location placeLocation = new Location(LocationManager.NETWORK_PROVIDER);
            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lon);
            centerMapOnLocation(placeLocation);
        }


        mMap.setOnMapLongClickListener(this);
    }
    public void centerMapOnLocation(Location location){
        if(location != null){
            Log.i("Info", "Here");
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        StringBuilder sb = new StringBuilder("");
        List<Address> address;
        try{
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (address != null){
                if(address.get(0).getThoroughfare() != null){
                        if(address.get(0).getSubThoroughfare() != null){
                            sb.append(address.get(0).getSubThoroughfare() + " ");
                        }
                        sb.append(address.get(0).getThoroughfare());
                }


                Log.i("INFO", sb.toString());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(sb.toString()));

        MainActivity.places.add(sb.toString());
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.company.memorableplaces", Context.MODE_PRIVATE);

        try {
            ArrayList<String> lats = new ArrayList<>();
            ArrayList<String> lon = new ArrayList<>();

            for(int i = 0; i< MainActivity.locations.size(); i++){
                lats.add(Double.toString((MainActivity.locations.get(i).latitude)));
                lon.add(Double.toString((MainActivity.locations.get(i).longitude)));
            }
            sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(lats)).apply();
            sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(lon)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Location saved!", Toast.LENGTH_LONG).show();
    }
}
