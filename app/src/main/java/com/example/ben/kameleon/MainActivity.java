package com.example.ben.kameleon;

import android.Manifest;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private WallpaperService mWallpaperService;
    private static final String TAG = "MainActivity";


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

        // Requests use of GPS coordinates
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10 );
            }
        }

        mFusedLocationClient = getFusedLocationProviderClient(this);

        getLastLocation();

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (mPreferences.getBoolean("selected_activate_button",true)) {
            scheduleJob(navigationView);
        }

    }


    public void scheduleJob(View v) {
        ComponentName componentName = new ComponentName(this, WallpaperService.class);
        JobInfo info = new JobInfo.Builder(100, componentName)
                .setPeriodic(30 * 1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        }
        else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(100);
        Log.d(TAG, "Job cancelled");
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

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location has not been enabled", Toast.LENGTH_LONG).show();
            return  ;
        }
        // If permissions have been granted, configure the gps refresh button
        else {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location.getLatitude(), location.getLongitude());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        }
    }


    public void onLocationChanged(double latitude, double longitude) {

        // Shows latitude for testing purposes
        // Toast.makeText(this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }
}