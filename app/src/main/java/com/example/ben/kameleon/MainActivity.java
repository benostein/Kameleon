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



        mFusedLocationClient = getFusedLocationProviderClient(this);

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (mPreferences.getBoolean("selected_activate_button",false)) {
            // If the app has been activated, schedule the wallpaper service job
            scheduleJob(navigationView);
        }

    }


    public void scheduleJob(View v) {
        // Links to the WallpaperService class
        ComponentName componentName = new ComponentName(this, WallpaperService.class);
        // Defines job information
        JobInfo info = new JobInfo.Builder(100, componentName)
                // Executes every 45 minutes (approx)
                .setPeriodic(45 * 60 * 1000)
                // Device must be connected to network for job to run
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                // Ensures the job runs after a reboot.
                .setPersisted(true)
                .build();
        // Schedules the job
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            // Shows job has been scheduled in app log
            Log.d(TAG, "Job scheduled");
        }
        else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob(View v) {
        // When job is cancelled (by clicking deactivate) stop scheduling the service
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(100);
        Log.d(TAG, "Job cancelled");
    }



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


}
