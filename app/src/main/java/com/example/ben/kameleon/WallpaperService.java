package com.example.ben.kameleon;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class WallpaperService extends JobService {

    private static final String TAG = "WallpaperJobService";
    private boolean jobCancelled = false;
    // Array of weather wallpaper drawables
    public static Integer[] weatherWallpapers = {

            R.drawable.img_wall_weather_801, R.drawable.img_wall_weather_601, R.drawable.img_wall_weather_200, R.drawable.img_wall_weather_500, R.drawable.img_wall_weather_800

    };
    public String currentSsid;
    public String widgetString;
    public int conditionWallpaper;



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "WallpaperService has started");

        // Links to shared preferences file
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);


        // If weather mode is selected, change the wallpaper depending on the weather condition
        if (!mPreferences.getBoolean("selected_weather_button", true)) {
            Log.d(TAG, "Weather mode has started");

            // Toast.makeText(getApplicationContext(), "Weather mode has started", Toast.LENGTH_LONG).show();

            // Gets the latest location
            getLastLocation();
            // Retrieves the latest weather information
            getWeatherData();
            
            // Gets the latest weather condition from shared preferences
            String conditionId = (mPreferences.getString("condition_id","801"));

            // Changes the wallpaper depending on the current condition ID
            changeWeatherWallpaper(jobParameters, conditionId);
        }
        if (!mPreferences.getBoolean("selected_wifi_button", true)) {
            Log.d(TAG, "Wi-Fi mode has started");

            // Gets the SSID of the current network and stores it as currentSsid
            getCurrentWifi();

            // Removes new line from SSID
            currentSsid = currentSsid.replace("\"", "");

            // Gets list of SSIDs (previously connected to) from shared preferences stored within the Wi-Fi mode array of SSIDs
            ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");

            String json = mPreferences.getString("wifi_array_list", null);

            try {
		        // Checks if the current SSID is within the list of SSIDs previously connected to (and configured) within the Wi-Fi mode
                if (json.contains(currentSsid) && currentSsid != null) {
			        // Retrieves index of this SSID
                    int index = getItemPos(wifiList, currentSsid);

			        // Locates the image assigned to this SSID
                    String wifiWallpaperPath = wifiList.get(index).getWifiWallpaper();

			        // Changes the wallpaper to the image specified in the path
                    changeWifiWallpaper(wifiWallpaperPath);
                }
            }
            catch (NullPointerException e) {
		        // If no wallpaper is configured or current SSID is not within the array, print an error in the log and do not change the wallpaper
                e.printStackTrace();
                Log.d(TAG, "Wallpaper image not configured.");
            }



        }
        else {
	        // If no mode has been selected, do nothing
            Log.d(TAG, "No mode selected");
        }
		
	    // Checks if a widget has been selected
        if (mPreferences.getInt("selected_widget", 0) != 0) {
            Log.d(TAG, "Widget selected");

		    // If the temperature widget has been selected, set the widget string to the current temp from shared preferences
            if (mPreferences.getInt("selected_widget", 0) == 1) {
                Log.d(TAG, "Temperature widget selected");
		    // If no value is found for current temperature, set it to the default of 10°C
                widgetString = mPreferences.getString("current_temp_string", "10°C");
            }
		    // If the humidity widget has been selected, set the widget string to the current humidity from shared preferences
            else if (mPreferences.getInt("selected_widget", 0) == 2) {
                Log.d(TAG, "Humidity widget selected");
		    // If no value is found for current humidity, set it to the default of 50%
                widgetString = mPreferences.getString("current_humidity_string", "50%");
            }
		    // If the wind speed widget has been selected, set the widget string to the current wind speed from shared preferences
            else if (mPreferences.getInt("selected_widget", 0) == 3) {
                Log.d(TAG, "Wind speed widget selected");
		    // If no value is found for current wind speed, set it to the default of 10m/s
                widgetString = mPreferences.getString("current_wind_speed_string", "10m/s");
            }

		    // Print in the log what the current widget string is
            Log.d(TAG, widgetString);


		    // Creates transparent canvas and sets the text style (white and shadow) and size
            int canvasWidth = 300;
            int canvasHeight = 130;
            Bitmap myForegroundBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(myForegroundBitmap);
            TextPaint textPaint = new TextPaint();
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(30 * getResources().getDisplayMetrics().density);
            textPaint.setColor(Color.WHITE);
            textPaint.setShadowLayer(2, 0, 4, R.color.colorWidgetShadow);
            //textPaint.setTypeface(Typeface.DEFAULT);

		    // Draws the widget string to the canvas and save this as an image
            canvas.drawText(widgetString, canvasWidth-6, canvasHeight-6, textPaint) ;
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

		    // Gets the current wallpaper
            Drawable wallpaperDrawable = getCurrentWallpaper();

		    // Converts the current wallpaper drawable into a bitmap
            Bitmap myBackgroundBitmap = drawableToBitmap(wallpaperDrawable);
			
		    // Combines the image with the widget string and the background into one image
            Bitmap WallpaperBitmap = combineImages(myBackgroundBitmap, myForegroundBitmap);

		    // Sets the wallpaper as the combined image
            changeWallpaper(WallpaperBitmap);

        }

        return true;
    }

    private Drawable getCurrentWallpaper() {
	    // Accesses the wallpaper manager and gets the current wallpaper/drawable
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
	    // Returns the drawable
        return wallpaperDrawable;
    }

    private void changeWallpaper(Bitmap wallpaper) {
	    // Accesses the wallpaper manager
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(this.getApplicationContext());

        try {
	    // Sets the device wallaper depending on the bitmap that has been passed into the function
            myWallpaperManager.setBitmap(wallpaper);
            Log.d("OnClick", "Wallpaper set");
        }
        catch (IOException e) {
	    // Prints error to log if unable to set wallpaper
            e.printStackTrace();
        }

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

	    // Checks if drawable it a bitmap drawable and returns bitmap version of drawable
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

	    // If drawable is not a bitmap drawable, create bitmap
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

	    // Adds drawable image to canvas and returns this as a bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        Bitmap cs;

	    // Gets the width and height of the device (in pixels)
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

	    // Defines new bitmap with the resolution of the dimensions of the device
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
	    // Sets the current wallpaper as the background in the canvas
        background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
	    // Places widget string image on top in top right corner
        comboImage.drawBitmap(foreground, (width-340), 50, null);

	    // Returns the combined image
        return cs;
    }

    private void changeWifiWallpaper(String wifiWallpaperPath) {
	    // Accesses the wallpaper manager
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(this.getApplicationContext());

	    // If the path to the drawable assigned to the current SSID is not empty
        if (wifiWallpaperPath != null) {
            try {
		    // Sets the device wallaper depending on the path of the drawable
                myWallpaperManager.setBitmap(BitmapFactory.decodeFile(wifiWallpaperPath));
                Log.d("OnClick", "Wallpaper set");
            }
            catch (IOException e) {
		    // Prints error to log if unable to set wallpaper
                e.printStackTrace();
            }
        }
    }

    private int getItemPos(ArrayList<WifiItem> mArrayList, String wifiName)
    {
        for(int i=0;i<mArrayList.size();i++)
        {
	        // Iterates through each item in the array until the current SSID is equal to the item in the array
            String arrayWifiName = mArrayList.get(i).getWifiName();
            if(arrayWifiName.equals(wifiName))
            {
		        // Returns the index of the SSID in the array
                return i;
            }
        }
        return -1;
    }

    public void saveArrayList(ArrayList<WifiItem> list, String key){
	    // Stores array in shared preferences as a JSON list
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<WifiItem> getArrayList(String key){
	    // Retrieves JSON list of WifiItems form shared preferences and converts them into an array list
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WifiItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void getCurrentWifi() {
	    // Accesses the wifi manager
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

	    // Gets the current connection info
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
	    // Gets the name of the SSID the device is currently connected to
            currentSsid = wifiInfo.getSSID();
        }
    }

    private void changeWeatherWallpaper(final JobParameters jobParameters, String conditionId) {

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


        // Defines WallpaperManager
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());

        // Accesses shared preferences file
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);


        // If the nature pack is selected, retrieve those wallpapers
        if(!mPreferences.getBoolean("selected_nature_pack_button", true)) {
            conditionWallpaper = getResources().getIdentifier("img_wall_nature_weather_" + weatherConditionIds.get(conditionId),"drawable", getPackageName());
        }
        // If the cartoon pack is selected, retrieve those wallpapers
        else if(!mPreferences.getBoolean("selected_cartoon_pack_button", true)) {
            conditionWallpaper = getResources().getIdentifier("img_wall_weather_" + weatherConditionIds.get(conditionId),"drawable", getPackageName());
        }

        try {
            // Sets the wallpaper to the located wallpaper as previously defined, depending on the pack selected
            myWallpaperManager.setResource(+conditionWallpaper);
        }
        catch (IOException e) {
            // Prints error to log
            e.printStackTrace();
        }
    }




    @Override
    public boolean onStopJob(JobParameters jobParameters) {
	    // Function is run if the job is cancelled
        Log.d(TAG, "Job cancelled before complete");
        jobCancelled = true;
        return true;
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

	    // Checks whther the location permissions have been enabled or not
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location has not been enabled", Toast.LENGTH_LONG).show();
            return  ;
        }
		
        // If permissions have been granted, get the device's last location
        else {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
				                // Passes the coordinates into onLocationChanged method
                                onLocationChanged(location.getLatitude(), location.getLongitude());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
			                // Error is generated if the location cannot be determined
                            Log.d(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        }
    }


    public void onLocationChanged(double latitude, double longitude) {
	    // Accesses shared preferences
        SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        if((latitude > 90) || (latitude < -90) || (longitude > 180) || (longitude < -180)) {

            Toast.makeText(this, "GPS coordinates are outside of range. Sensor may be faulty. Weather data may be inaccurate.", Toast.LENGTH_SHORT).show();

            if(latitude > 90) {
                latitude = 90;
            }
            else if (latitude < -90) {
                latitude = -90;
            }
            if (longitude > 180) {
                longitude = 180;
            }
            else if (latitude < -180) {
                latitude = -180;
            }
        }

        // Shows latitude for testing purposes
        // Toast.makeText(this, "Latitude: " + String.valueOf(latitude) + "\n" + "Longitude: " + String.valueOf(longitude), Toast.LENGTH_SHORT).show();

        // Stores latitude and longitude values in shared preferences as strings as Android does not support storing doubles and floats would lose accuracy
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }

    public void getWeatherData() {
	    // Accesses shared preferences
        final SharedPreferences mPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();

        // Retrieves location coordinates from SharedPreferences and converts them into a double
        double latitude = Double.valueOf(mPreferences.getString("latitude","0"));
        double longitude = Double.valueOf(mPreferences.getString("longitude","0"));

        // Pulls in the API key from the BuildConfig file
        String apiKey = BuildConfig.openWeatherMapApiKey;

        // Forms a url to the OpenWeatherMap API and concatenates the retrieved latitude and longitude coordinates as well as the API key to retrieve relevant weather data
        String weatherDataUrl ="http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";

        // Creates a JsonObjectResponse that performs the onResponse() method once it has received a JsonObject from the url specified
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, weatherDataUrl, null, new Response.Listener<JSONObject>() {
            // When a JsonObject is received, do this:
            @Override
            public void onResponse(JSONObject response) {
                try {

                    // Retrieves specific elements from the JsonObject and assigns them to their corresponding variable names
                    JSONObject main_object = response.getJSONObject("main");
                    JSONObject wind_object = response.getJSONObject("wind");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getInt("humidity"));
                    String windSpeed = String.valueOf(wind_object.getDouble("speed"));
//                    String description = object.getString("description");
//                    String city = response.getString("name");
                    String weatherCode = object.getString("id");

                    editor.putString("condition_id", String.valueOf(weatherCode));

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formatted_date = sdf.format(calendar.getTime());

                    double temperature = Double.parseDouble(temp);
                    int temperatureInt;
                    String temperatureString;

                    if (mPreferences.getInt("selected_temp", 0) == 1) {
                        temperature = ((9/5) * temperature + 32);
                        temperatureInt = (int)(Math.round(temperature));
                        temperatureString = String.valueOf(temperatureInt) + (char) 0x00B0 + "F";
                    }
                    else {
                        temperatureInt = (int)(Math.round(temperature));
                        temperatureString = String.valueOf(temperatureInt) + (char) 0x00B0 + "C";
                    }


                    editor.putInt("current_temp", temperatureInt);
                    editor.putString("current_temp_string", temperatureString);
                    editor.putString("current_humidity_string", (humidity + "%"));
                    editor.putString("current_wind_speed_string", (windSpeed + "m/s"));


                    editor.apply();


                    // Toast for testing
                    // Toast.makeText(getApplicationContext(), String.valueOf(weatherCode), Toast.LENGTH_LONG).show();

                }

                catch(JSONException e) {
		            // Prints error to log
                    e.printStackTrace();
                }
            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "Error retrieving weather data");
				//e.printStackTrace();
            }
        });

	    // Adds weather data request to queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jor);

    }
}
