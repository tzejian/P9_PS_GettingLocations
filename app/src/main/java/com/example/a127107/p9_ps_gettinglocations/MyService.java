package com.example.a127107.p9_ps_gettinglocations;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {
    boolean started;
    String folderLocation;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Service", "Created: ");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(started == false){
            started = true;




            Log.d("service","Started");
        }else{
            Log.d("service","Still running");
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("service","Exited");
        super.onDestroy();

    }
}

