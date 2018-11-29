package com.example.ben.kameleon;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class LocationService extends Service {

//    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 0;

//    // Creates a new shared preferences file that allows user preferences to be stored within the application
//    SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
//    // Allows preferences file to be edited using 'editor'
//    final SharedPreferences.Editor editor = mPreferences.edit();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LocationListener implements  android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            mLastLocation.set(location);

//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//            // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
//            editor.putString("latitude", String.valueOf(latitude));
//            editor.putString("longitude", String.valueOf(longitude));
//            editor.apply();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        initializeLocationManager();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
