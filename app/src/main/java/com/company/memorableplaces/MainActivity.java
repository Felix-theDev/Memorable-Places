package com.company.memorableplaces;
/** Program to save memorable places you'll like to visit
 * @author Felix Ogbonnaya
 * @since 2020-05-22
 */
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static List<LatLng> locations = new ArrayList<>();
    LocationManager locationManager;
    LocationListener locationListener;
    ListView listView;
    static ArrayList<String> places = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    double lat = -34;
    double lon = 151;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, locationListener);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.company.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longitude = new ArrayList<>();

        places.clear();
        latitude.clear();
        longitude.clear();
        locations.clear();

        try{
            places =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<>())));
            latitude =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<>())));
            longitude =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<>())));

        }catch (Exception e){
            e.printStackTrace();
        }

        if (places.size() > 0 && latitude.size() > 0 && longitude.size()>0){
            if(places.size() == latitude.size() && (places.size() ==longitude.size())){
                for (int i =0; i< latitude.size(); i++){
                    locations.add(new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))));
                }
            }
        }
        else {
            places.add("Add a new Place");
            locations.add(new LatLng(0, 0));
        }

        listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    Log.i("INFO", "Clicked");
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("index", 0);
                    startActivity(intent);

                }
                else {
                    double latitude = locations.get(i).latitude;
                    double longitude = locations.get(i).longitude;
                    Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
                    intent1.putExtra("latitude", latitude);
                    intent1.putExtra("longitude", longitude);
                    intent1.putExtra("index", i);
                    startActivity(intent1);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
            }

        }

    }
}

