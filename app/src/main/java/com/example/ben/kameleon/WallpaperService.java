package com.example.ben.kameleon;

import android.app.Service;
import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class WallpaperService extends JobService {

    private static final String TAG = "WallpaperJobService";
    private Random rand = new Random();
    private boolean jobCancelled = false;
    public static Integer[] weatherWallpapers = {

            R.drawable.img_wallpaper_801, R.drawable.img_wallpaper_601, R.drawable.img_wallpaper_200, R.drawable.img_wallpaper_500, R.drawable.img_wallpaper_800

    };


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "WallpaperService has started");
        changeWallpaper(jobParameters);
        return true;
    }

    private void changeWallpaper(final JobParameters jobParameters) {
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());

        try {
            myWallpaperManager.setResource(+ getRandomWallpaper());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
// LatLng currentLocation = getLastLocation();
        // getWeatherData(currentLocation);
        getLastLocation();
        getWeatherData();

    }

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

        // Shows latitude for testing purposes
        // Toast.makeText(this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();

        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }

    public void getWeatherData() {

        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);

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
                    // Locates the TextViews within the fragment and assigns them to variables so they are easier to access
//                    TextView currentCity = getView().findViewById(R.id.current_city);
//                    TextView currentTemp = getView().findViewById(R.id.current_temp);
//                    TextView currentDate = getView().findViewById(R.id.current_date);
//                    TextView currentWeather = getView().findViewById(R.id.current_weather);

                    // Retrieves specific elements from the JsonObject and assings them to their corresponding variable names
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

                    Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_LONG).show();

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
