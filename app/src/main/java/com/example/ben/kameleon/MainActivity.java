package com.example.ben.kameleon;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private DrawerLayout mDrawerLayout;
    GoogleApiClient mGoogleApiClient;

    ProgressBar _progressBar;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    // Button weatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets theme for application
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        // Sets content on launch
        setContentView(R.layout.activity_main);

        // Sets custom toolbar as default toolbar and imports menu icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Locates, defines and creates navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Displays the home fragment
        displaySelectedScreen(R.id.nav_home);

        // Forces the app to be portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


//        wifiButton.setOnClickListener(this);
//        tempButton.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            getLocation();
        }
        else {
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
        }


    }




//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.weather_mode_button:
//                Toast.makeText(this, "Tester!", Toast.LENGTH_LONG).show();
//                break;
//            case R.id.wifi_mode_button:
//                // do your code
//                break;
//            case R.id.temp_mode_button:
//                // do your code
//                break;
//            default:
//                break;
//        }
//
//    }


    // Allows menu button to open navigation panel when pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Allows back button on device to close navigation drawer
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // When an item in the navigation drawer has been selected, pass the id of the item into the method below
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        // Creates the fragment object
        Fragment fragment = null;

        // Checks which object has been selected in the navigation drawer
        switch (itemId) {
            case R.id.nav_home:
                fragment = new MainFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_widgets:
                fragment = new WidgetsFragment();
                break;
        }

        // Replaces the fragment with the object selected in navigation drawer
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        // Closes drawer once item in navigation drawer has been selected
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    /*Ending the updates for the location service*/
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            /*Getting the location after acquiring location service*/
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                _progressBar.setVisibility(View.INVISIBLE);

                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                updateStoredCoordinates(latitude, longitude);

            }

            else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                 * So we will get the current location.
                 * For this we'll implement Location Listener and override onLocationChanged*/
                Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
            }
        }
    }

    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        _progressBar.setVisibility(View.INVISIBLE);

        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();

        updateStoredCoordinates(latitude, longitude);
    }

    public void updateStoredCoordinates(double latitude, double longitude) {

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }
}