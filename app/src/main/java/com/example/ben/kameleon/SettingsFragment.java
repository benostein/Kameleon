package com.example.ben.kameleon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // When view is being created, do this:

        // Gets the current view on the device and allows for easier access of resources
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Finds the settings spinner in the XML and gives it a variable to access
        Spinner settingsSpinner = v.findViewById(R.id.temp_setting_spinner);

        // Defines new array list
        ArrayList<String> settingsArray = new ArrayList<>();

        // Adds each item to the settings array
        settingsArray.add("Celsius");
        settingsArray.add("Fahrenheit");
        settingsArray.add("Kelvin");

        // Creates an adapter that allows an array to be used as the contents of the spinner
        ArrayAdapter<String> settingsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, settingsArray);
        // Uses the adapter to set the values of the array to the contents and style of the spinner
        settingsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        settingsSpinner.setAdapter(settingsAdapter);
        
        // Sets the action bar at the top of the app to say the current mode
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
