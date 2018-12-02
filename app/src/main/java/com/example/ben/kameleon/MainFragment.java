package com.example.ben.kameleon;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainFragment extends Fragment implements View.OnClickListener {

    boolean activated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // When view is being created, do this:

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Sets the action bar at the top of the app to say the current mode
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Kameleon");

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Locates and assigns variable to activate button
        Button activateButton = v.findViewById(R.id.activate_button);
        Button deactivateButton = v.findViewById(R.id.deactivate_button);
        // Locates and assigns variable to each button
        Button weatherButton = v.findViewById(R.id.weather_mode_button);
        Button wifiButton = v.findViewById(R.id.wifi_mode_button);
        Button tempButton = v.findViewById(R.id.temp_mode_button);
        // Locates and assigns variable to each card
        CardView weatherCard = v.findViewById(R.id.weather_mode_card);
        CardView wifiCard = v.findViewById(R.id.wifi_mode_card);
        CardView tempCard = v.findViewById(R.id.temp_mode_card);

        // Button listeners
        activateButton.setOnClickListener(this);
        deactivateButton.setOnClickListener(this);
        weatherButton.setOnClickListener(this);
        wifiButton.setOnClickListener(this);
        tempButton.setOnClickListener(this);
        // Card listeners
        weatherCard.setOnClickListener(this);
        wifiCard.setOnClickListener(this);
        tempCard.setOnClickListener(this);

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        // Restores user selected value in spinner from value in shared preferences
        activateButton.setEnabled(mPreferences.getBoolean("selected_activate_button",true));
        deactivateButton.setEnabled(mPreferences.getBoolean("selected_deactivate_button",false));
        // Enables weather mode by default
        weatherButton.setEnabled(mPreferences.getBoolean("selected_weather_button",false));
        wifiButton.setEnabled(mPreferences.getBoolean("selected_wifi_button",true));
        tempButton.setEnabled(mPreferences.getBoolean("selected_temp_button",true));

        TextView latitudeText = v.findViewById(R.id.current_latitude);
        TextView longitudeText = v.findViewById(R.id.current_longitude);

//        if(!runtimePermissions()) {
//            activated = true;
//            // Restores user's last location in settings
//            latitudeText.setText(mPreferences.getString("latitude", "0"));
//            longitudeText.setText(mPreferences.getString("longitude", "0"));
//        }

        latitudeText.setText(mPreferences.getString("latitude", "0"));
        longitudeText.setText(mPreferences.getString("longitude", "0"));

        getWeatherData();


        return v;
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                activated = true;
            }
            else {
                runtimePermissions();
            }
        }
    }

    private boolean runtimePermissions() {
        if(Build.VERSION.SDK_INT >= 23  && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }






    @Override
    public void onClick(View view) {

        // Locates and assigns variable to each button
        Button activateButton = getView().findViewById(R.id.activate_button);
        Button deactivateButton = getView().findViewById(R.id.deactivate_button);
        Button weatherButton = getView().findViewById(R.id.weather_mode_button);
        Button wifiButton = getView().findViewById(R.id.wifi_mode_button);
        Button tempButton = getView().findViewById(R.id.temp_mode_button);

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);


        switch (view.getId()) {
            case R.id.activate_button:
                Toast.makeText(getActivity(), "Activated", Toast.LENGTH_SHORT).show();
                activateButton.setEnabled(false);
                deactivateButton.setEnabled(true);

//                if (activated == true) {
//                    Intent i = new Intent(getActivity(), LocationService.class);
//                    getActivity().startService(i);
//                }

                TextView latitudeText = getView().findViewById(R.id.current_latitude);
                TextView longitudeText = getView().findViewById(R.id.current_longitude);

                // Calls the getLastLocation method from the main activity
                ((MainActivity) getActivity()).getLastLocation();
                getWeatherData();


                ((MainActivity) getActivity()).scheduleJob(view);



                break;

            case R.id.deactivate_button:
                Toast.makeText(getActivity(), "Deactivated", Toast.LENGTH_SHORT).show();
                activateButton.setEnabled(true);
                deactivateButton.setEnabled(false);

                ((MainActivity) getActivity()).cancelJob(view);

                break;

            case R.id.weather_mode_button:
                // Displays toast message
                Toast.makeText(getActivity(), "Weather Mode", Toast.LENGTH_SHORT).show();

                // Disables button when pressed and enables other buttons
                weatherButton.setEnabled(false);
                wifiButton.setEnabled(true);
                tempButton.setEnabled(true);
                break;
            case R.id.wifi_mode_button:
                // Displays toast message
                Toast.makeText(getActivity(), "Wi-Fi Mode", Toast.LENGTH_SHORT).show();

                // Disables button when pressed and enables other buttons
                weatherButton.setEnabled(true);
                wifiButton.setEnabled(false);
                tempButton.setEnabled(true);
                break;
            case R.id.temp_mode_button:
                // Displays toast message
                Toast.makeText(getActivity(), "Temperature Mode", Toast.LENGTH_SHORT).show();

                // Disables button when pressed and enables other buttons
                weatherButton.setEnabled(true);
                wifiButton.setEnabled(true);
                tempButton.setEnabled(false);
                break;


            case R.id.weather_mode_card:
                // Changes view to weather mode fragment
                WeatherModeFragment fragment = new WeatherModeFragment();
//                fragment.setEnterTransition(new Slide(Gravity.END));
//                fragment.setExitTransition(new Slide(Gravity.START));

                replaceFragment(fragment);
                break;
            case R.id.wifi_mode_card:
                // Displays toast message
                Toast.makeText(getActivity(), "Wi-Fi Mode Card", Toast.LENGTH_SHORT).show();
                break;
            case R.id.temp_mode_card:
                // Displays toast message
                Toast.makeText(getActivity(), "Temperature Mode Card", Toast.LENGTH_SHORT).show();
                break;
        }

            // Saves the state of the mode buttons
            saveButtons(activateButton, deactivateButton, weatherButton, wifiButton, tempButton);
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // on Settings Fragment load, code goes here
    }

    public void saveButtons(Button activateButton, Button deactivateButton, Button weatherButton, Button wifiButton, Button tempButton) {
        // Accesses the shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Allows preferences file to be edited using 'editor'
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Stores the state of the home buttons in shared preferences, so choice is stored when app is closed
        editor.putBoolean("selected_activate_button", activateButton.isEnabled());
        editor.putBoolean("selected_deactivate_button", deactivateButton.isEnabled());
        editor.putBoolean("selected_weather_button", weatherButton.isEnabled());
        editor.putBoolean("selected_wifi_button", wifiButton.isEnabled());
        editor.putBoolean("selected_temp_button", tempButton.isEnabled());
        editor.apply();
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void getWeatherData() {

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        double latitude = Double.valueOf(mPreferences.getString("latitude","0"));
        double longitude = Double.valueOf(mPreferences.getString("longitude","0"));

        String apiKey = BuildConfig.openWeatherMapApiKey;

        String url ="http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=imperial";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    TextView currentCity = getView().findViewById(R.id.current_city);
                    TextView currentTemp = getView().findViewById(R.id.current_temp);
                    TextView currentDate = getView().findViewById(R.id.current_date);
                    TextView currentWeather = getView().findViewById(R.id.current_weather);

                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formatted_date = sdf.format(calendar.getTime());

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) /1.8000;
                    centi = Math.round(centi);
                    int i = (int)centi;

                    currentCity.setText(city);
                    currentTemp.setText(String.valueOf(i) + "°");
                    currentWeather.setText(description.substring(0,1).toUpperCase() + description.substring(1));
                    currentDate.setText(formatted_date);

                }

                catch(JSONException e) {
                    e.printStackTrace();
                }
            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);

    }

}

