package com.example.ben.kameleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

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
                // If nothing is selected, do this:
            }
        });

        // Finds the settings spinner in the XML and gives it a variable to access
        final Spinner refreshTimeSpinner = v.findViewById(R.id.refresh_time_spinner);

        // Defines new array list
        ArrayList<String> refreshTimeArray = new ArrayList<>();

        // Adds each item to the settings array
        refreshTimeArray.add("30 Min");
        refreshTimeArray.add("1 Hour");
        refreshTimeArray.add("2 Hours");
        refreshTimeArray.add("4 Hours");

        // Creates an adapter that allows an array to be used as the contents of the spinner
        ArrayAdapter<String> refreshTimeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, refreshTimeArray);
        // Uses the adapter to set the values of the array to the contents and style of the spinner
        refreshTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshTimeSpinner.setAdapter(refreshTimeAdapter);

        // Restores user selected value in spinner from value in shared preferences
        refreshTimeSpinner.setSelection(mPreferences.getInt("selected_refresh_time",1));

        // Method for listening which item has been selected in the spinner
        refreshTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                // When an item in the list is selected, store the position value in the shared preferences, so selection is stored for next time.
                int refreshTimeSpinnerValue = refreshTimeSpinner.getSelectedItemPosition();
                editor.putInt("selected_refresh_time", refreshTimeSpinnerValue);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If nothing is selected, do this:
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
