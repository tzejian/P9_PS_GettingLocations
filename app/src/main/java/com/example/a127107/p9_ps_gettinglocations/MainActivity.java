package com.example.a127107.p9_ps_gettinglocations;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    TextView tvLat,tvLong,tvCurrLat,tvCurrLong;
    Button btnStart,btnStop,btnCheck;
    String folderLocation;
    private GoogleMap map;
    private Marker now;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        tvLat = (TextView)findViewById(R.id.tvLat);
        tvLong = (TextView)findViewById(R.id.tvLong);
        tvCurrLat = (TextView)findViewById(R.id.tvCurLat);
        tvCurrLong = (TextView)findViewById(R.id.tvCurLong);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnCheck = (Button) findViewById(R.id.btnCheck);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //folder

        folderLocation = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/P9_Records";

        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            }
        }
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read the data
                File targetFile = new File(folderLocation, "data.txt");

                //read file line by line
                if (targetFile.exists() == true){
                    String data ="";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null){
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to read!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, data,
                            Toast.LENGTH_LONG).show();

                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);
                Toast.makeText(MainActivity.this, "Service Started",
                        Toast.LENGTH_LONG).show();

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
                Toast.makeText(MainActivity.this, "Service Stop",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                ||  permissionCheck_Fine  == PermissionChecker.PERMISSION_GRANTED){
            mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest
                    .PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);

        } else {
            mLocation = null;
            Toast.makeText(MainActivity.this,
                    "Permission not granted to retrieve location info",
                    Toast.LENGTH_SHORT).show();
        }

        if (mLocation != null) {
            //set the text to the lat and long in textview
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double lag = lastKnown.getLatitude();
            double lng = lastKnown.getLongitude();
            tvLat.setText(""+ lag);
            tvLong.setText("" + lng);
            

        }
         else {
            Toast.makeText(this, "Location not Detected",
                    Toast.LENGTH_SHORT).show();
        }

        // map fragment
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);


        //meant to to get the reference to the Google Map object
        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                //set the longitude and latitude
                double x = mLocation.getLongitude();
                double y = mLocation.getLatitude();


                LatLng poi_CausewayPoint = new LatLng(y,x);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_CausewayPoint,
                        11));

                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                }

                UiSettings ui = map.getUiSettings();
                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        //the detected location is given by the variable location in the signature
        tvLat.setText(""+ mLocation.getLatitude());
        tvLong.setText("" + mLocation.getLongitude());

        File targetFile = new File(folderLocation, "data.txt");

        try {
            FileWriter writer = new FileWriter(targetFile, true);
            writer.write(mLocation.getLatitude()+", " + mLocation.getLongitude()+"\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to write!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if(now != null){
            now.remove();

        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double lat = latLng.latitude;
                double lng = latLng.longitude;
                tvCurrLat.setText(""+lat);
                tvCurrLong.setText(""+lng);


                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}