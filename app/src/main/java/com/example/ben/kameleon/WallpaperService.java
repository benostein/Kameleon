package com.example.ben.kameleon;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class WallpaperService extends JobService {

    private static final String TAG = "WallpaperJobService";
    private Random rand = new Random();
    private boolean jobCancelled = false;
    public static Integer[] weatherWallpapers = {

            R.drawable.img_wall_weather_801, R.drawable.img_wall_weather_601, R.drawable.img_wall_weather_200, R.drawable.img_wall_weather_500, R.drawable.img_wall_weather_800

    };



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "WallpaperService has started");

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);


        // If weather mode is selected, change the wallpaper depending on the weather condition
        if (mPreferences.getBoolean("selected_weather_button", true)) {
            Log.d(TAG, "Weather mode has started");

            Toast.makeText(getApplicationContext(), "Weather mode has started", Toast.LENGTH_LONG).show();

            getLastLocation();
            getWeatherData();
            String conditionId = (mPreferences.getString("condition_id","801"));

            changeWallpaper(jobParameters, conditionId);
        }
        else {
            Log.d(TAG, "Weather mode did not start");

            Toast.makeText(getApplicationContext(), "Weather mode did not start", Toast.LENGTH_LONG).show();
        }


        return true;
    }

    private void changeWallpaper(final JobParameters jobParameters, String conditionId) {

        // HashMap to map all weather condition ids to specific images
        HashMap<String, String> weatherConditionIds = new HashMap<>();

        // Thunderstorm
        weatherConditionIds.put("200", "200");
        weatherConditionIds.put("201", "200");
        weatherConditionIds.put("202", "200");
        weatherConditionIds.put("210", "200");
        weatherConditionIds.put("211", "200");
        weatherConditionIds.put("212", "200");
        weatherConditionIds.put("221", "200");
        weatherConditionIds.put("230", "200");
        weatherConditionIds.put("231", "200");
        weatherConditionIds.put("232", "200");

        // Drizzle
        weatherConditionIds.put("300", "500");
        weatherConditionIds.put("301", "500");
        weatherConditionIds.put("302", "500");
        weatherConditionIds.put("310", "500");
        weatherConditionIds.put("311", "500");
        weatherConditionIds.put("312", "500");
        weatherConditionIds.put("313", "500");
        weatherConditionIds.put("314", "500");
        weatherConditionIds.put("321", "500");

        // Rain
        weatherConditionIds.put("500", "500");
        weatherConditionIds.put("501", "500");
        weatherConditionIds.put("502", "500");
        weatherConditionIds.put("503", "500");
        weatherConditionIds.put("504", "500");
        weatherConditionIds.put("511", "500");
        weatherConditionIds.put("520", "500");
        weatherConditionIds.put("521", "500");
        weatherConditionIds.put("522", "500");
        weatherConditionIds.put("531", "500");

        // Snow
        weatherConditionIds.put("600", "601");
        weatherConditionIds.put("601", "601");
        weatherConditionIds.put("602", "601");
        weatherConditionIds.put("611", "601");
        weatherConditionIds.put("612", "601");
        weatherConditionIds.put("615", "601");
        weatherConditionIds.put("616", "601");
        weatherConditionIds.put("620", "601");
        weatherConditionIds.put("621", "601");
        weatherConditionIds.put("622", "601");


        // Mist
        weatherConditionIds.put("701", "701");
        weatherConditionIds.put("711", "701");
        weatherConditionIds.put("721", "701");
        weatherConditionIds.put("731", "701");
        weatherConditionIds.put("741", "701");
        weatherConditionIds.put("751", "701");
        weatherConditionIds.put("761", "701");
        weatherConditionIds.put("762", "701");
        weatherConditionIds.put("771", "701");
        weatherConditionIds.put("781", "701");

        // Clear sky
        weatherConditionIds.put("800", "800");

        // Partial sun
        weatherConditionIds.put("801", "801");
        weatherConditionIds.put("802", "801");

        // Overcast
        weatherConditionIds.put("803", "803");
        weatherConditionIds.put("804", "803");


        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());

        int weatherConditionWallpaper = getResources().getIdentifier("img_wall_weather_" + weatherConditionIds.get(conditionId),"drawable", getPackageName());

        try {
            myWallpaperManager.setResource(+weatherConditionWallpaper);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

// LatLng currentLocation = getLastLocation();
        // getWeatherData(currentLocation);



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before complete");
        jobCancelled = true;
        return true;
    }

    public int getRandomWallpaper(){
        return weatherWallpapers[rand.nextInt(weatherWallpapers.length)];
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

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Shows latitude for testing purposes
        // Toast.makeText(this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();



        // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }

    public void getWeatherData() {

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Retrieves location coordinates from SharedPreferences and converts them into a double
        double latitude = Double.valueOf(mPreferences.getString("latitude","0"));
        double longitude = Double.valueOf(mPreferences.getString("longitude","0"));

        // Pulls in the API key from the BuildConfig file
        String apiKey = BuildConfig.openWeatherMapApiKey;

        // Forms a url to the OpenWeatherMap API and concatenates the retrieved latitude and longitude coordinates as well as the API key to retrieve relevant weather data
        String weatherDataUrl ="http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=imperial";

        // Creates a JsonObjectResponse that performs the onResponse() method once it has received a JsonObject from the url specified
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, weatherDataUrl, null, new Response.Listener<JSONObject>() {
            // When a JsonObject is received, do this:
            @Override
            public void onResponse(JSONObject response) {
                try {

                    // Retrieves specific elements from the JsonObject and assings them to their corresponding variable names
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");
                    String weatherCode = object.getString("id");

                    editor.putString("condition_id", String.valueOf(weatherCode));
                    editor.apply();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formatted_date = sdf.format(calendar.getTime());

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) /1.8000;
                    centi = Math.round(centi);
                    int i = (int)centi;

                    // Toast for testing
                    // Toast.makeText(getApplicationContext(), String.valueOf(weatherCode), Toast.LENGTH_LONG).show();

//                    currentCity.setText(city);
//                    currentTemp.setText(String.valueOf(i) + "°");
//                    currentWeather.setText(description.substring(0,1).toUpperCase() + description.substring(1));
//                    currentDate.setText(formatted_date);

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

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jor);

    }
}
