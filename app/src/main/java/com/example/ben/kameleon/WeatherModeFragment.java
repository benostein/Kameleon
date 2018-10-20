package com.example.ben.kameleon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WeatherModeFragment extends Fragment {

    // TODO (1) Implement back button in fragment

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

        final Button cartoonPackButton = v.findViewById(R.id.cartoon_pack_button);
        final Button naturePackButton = v.findViewById(R.id.nature_pack_button);

        cartoonPackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartoonPackButton.setEnabled(false);
                naturePackButton.setEnabled(true);
            }
        });

        naturePackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartoonPackButton.setEnabled(true);
                naturePackButton.setEnabled(false);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment fragment = new MainFragment();
                replaceFragment(fragment);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
