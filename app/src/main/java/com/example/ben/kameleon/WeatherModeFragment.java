package com.example.ben.kameleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class WeatherModeFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // When view is being created, do this:

        // Sets the current view on the device and allows for easier access of resources
        View v = inflater.inflate(R.layout.fragment_weather_mode, container, false);

        // Sets the action bar at the top of the app to say the current mode
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Weather Mode");

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        // Sets the back button in toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back);

        // Locates and assigns variable to each button
        Button cartoonPackButton = v.findViewById(R.id.cartoon_pack_button);
        Button naturePackButton = v.findViewById(R.id.nature_pack_button);

        // Button listeners
        cartoonPackButton.setOnClickListener(this);
        naturePackButton.setOnClickListener(this);

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Allows preferences file to be edited using 'editor'
        final SharedPreferences.Editor editor = mPreferences.edit();
        // Restores user selected value in spinner from value in shared preferences
        cartoonPackButton.setEnabled(mPreferences.getBoolean("selected_cartoon_pack_button",true));
        naturePackButton.setEnabled(mPreferences.getBoolean("selected_nature_pack_button",false));


        // Allows back button on toolbar to go back to main fragment
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment fragment = new MainFragment();
                replaceFragment(fragment);
            }
        });

        // Allows back button on device to go back to main fragment
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        MainFragment fragment = new MainFragment();
                        replaceFragment(fragment);
                        return true;
                    }
                }
                return false;
            }
        });

        return v;
    }

    public void onClick(View view) {

        // Locates and assigns variable to each button
        Button cartoonPackButton = getView().findViewById(R.id.cartoon_pack_button);
        Button naturePackButton = getView().findViewById(R.id.nature_pack_button);

        switch (view.getId()) {
            case R.id.cartoon_pack_button:
                cartoonPackButton.setEnabled(false);
                naturePackButton.setEnabled(true);
                break;
            case R.id.nature_pack_button:
                cartoonPackButton.setEnabled(true);
                naturePackButton.setEnabled(false);
        }

        // Saves the state of the mode buttons
        saveModeButtons(cartoonPackButton, naturePackButton);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void saveModeButtons(Button cartoonPackButton, Button naturePackButton) {
        // Accesses the shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Allows preferences file to be edited using 'editor'
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Stores the state of the home buttons in shared preferences, so choice is stored when app is closed
        editor.putBoolean("selected_cartoon_pack_button", cartoonPackButton.isEnabled());
        editor.putBoolean("selected_nature_pack_button", naturePackButton.isEnabled());
        editor.apply();
    }
    
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
