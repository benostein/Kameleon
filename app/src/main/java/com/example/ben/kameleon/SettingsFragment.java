package com.example.ben.kameleon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // When view is being created, do this:

        // Gets the current view on the device and allows for easier access of resources
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Sets the action bar at the top of the app to say the current mode
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        // Finds the settings spinner in the XML and gives it a variable to access
        final Spinner settingsSpinner = v.findViewById(R.id.temp_setting_spinner);

        // Defines new array list
        ArrayList<String> settingsArray = new ArrayList<>();

        // Adds each item to the settings array
        settingsArray.add("Celsius");
        settingsArray.add("Fahrenheit");

        // Creates an adapter that allows an array to be used as the contents of the spinner
        ArrayAdapter<String> settingsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, settingsArray);
        // Uses the adapter to set the values of the array to the contents and style of the spinner
        settingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settingsSpinner.setAdapter(settingsAdapter);

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Allows preferences file to be edited using 'editor'
        final SharedPreferences.Editor editor = mPreferences.edit();
        // Restores user selected value in spinner from value in shared preferences
        settingsSpinner.setSelection(mPreferences.getInt("selected_temp",0));

        // Method for listening which item has been selected in the spinner
        settingsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                // When an item in the list is selected, store the position value in the shared preferences, so selection is stored for next time.
                int settingsSpinnerValue = settingsSpinner.getSelectedItemPosition();
                editor.putInt("selected_temp", settingsSpinnerValue);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // code goes here
            }
        });

        // Finds the coordinates text in the XML and gives it a variable to access
        final TextView gpsCoordinatesText = v.findViewById(R.id.gps_refresh_coords);

        // Restores user's last location in settings
        gpsCoordinatesText.setText("Latitude: " + (mPreferences.getString("latitude","0")) + "\n" + "Longitude: " + (mPreferences.getString("longitude","0")));

        // Initialises location manager and listener so device location can be accessed
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            // When location changes, do this:
            @Override
            public void onLocationChanged(Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                gpsCoordinatesText.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);

                // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
                editor.putString("latitude", String.valueOf(latitude));
                editor.putString("longitude", String.valueOf(longitude));
                editor.apply();
            }

            // If location status has changed, do this:
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            // If location services have been enabled, do this:
            @Override
            public void onProviderEnabled(String s) {

            }

            // If location services have been disabled, do this:
            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

//        // Checks whether permissions for location and internet have been enabled or not
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
//                }, 10 );
//            }
//        }
//        // If permissions have been granted, configure the gps refresh button
//        else {
//            configureGpsButton();
//        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    private void configureGpsButton() {
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                        PackageManager.PERMISSION_GRANTED) {
//
//            // Finds the gps refresh button in the XML and gives it a variable to access
//            Button gpsRefreshButton = getView().findViewById(R.id.gps_refresh_button);
//
//            // Listens for when button is pressed
//            gpsRefreshButton.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("MissingPermission")
//                @Override
//                public void onClick(View view) {
//                    // Requests device location using GPS in 1000ms intervals
//                    locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
//                    try {
//                        // Waits 8 seconds until GPS location has stabilised
//                        Thread.sleep(8000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    // Stops retrieving GPS data
//                    locationManager.removeUpdates(locationListener);
//                }
//
//            });
//        }
//
//        else {
//            requestPermissions(new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
//            }, 10 );
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 10:
//                // if the permissions have been granted, configure the GPS button
//                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    configureGpsButton();
//                return;
//        }
//    }

}
