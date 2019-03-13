package com.example.ben.kameleon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class WifiModeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private WifiAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<WifiItem> wifiList;
    private String selectedImagePath;
    String mFilePath;
    private static int RESULT_LOAD_IMG = 1;
    String imgPath;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // When view is being created, do this:

        // Sets the current view on the device and allows for easier access of resources
        View v = inflater.inflate(R.layout.fragment_wifi_mode, container, false);

        // Sets the action bar at the top of the app to say the current mode
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Wi-Fi Mode");

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        // Sets the back button in toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back);


        // Creates a new shared preferences file that allows user preferences to be stored within the application
        SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Allows preferences file to be edited using 'editor'
        final SharedPreferences.Editor editor = mPreferences.edit();



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

        // Creates ArrayList of WifiItems
        ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");

        // If wifiList is empty, get the Wi-Fi networks stored on the device and populate the wifiList
        if (wifiList == null) {
            createWifiList();
            wifiList = getArrayList("wifi_array_list");
        }

        // Locates recycler view in Wi-Fi mode fragment
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        // Defines that the recycler view of Wi-Fi networks will not change when being viewed
        mRecyclerView.setHasFixedSize(true);
        // Accesses layout manager and wifi adapter class
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WifiAdapter(wifiList);

        // Sets the layout of the recycler view to the current activity and sets the data adapter to the wifi adapter class
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // When an item in the recycler view is clicked on, do this:
        mAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                // Accesses shared preferences file
                SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = mPreferences.edit();

                // Store the position of the item clicked on within shared preferences
                editor.putInt("temp_wifi_position", position);
                editor.apply();

                // Launches an intent that allows the user to select an image from the gallery
                loadImageFromGallery();

            }
        });

        return v;
    }

    public void loadImageFromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an image is picked, do this:
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                
                // Get the image from the intent data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.MediaColumns.DATA };
                String[] fileSize = { MediaStore.MediaColumns.SIZE };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        null, null, null, null);
                // Move to first row
                cursor.moveToFirst();


                // Uses cursors position to locate selected image path
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                // Uses cursors position to select size of image
                int sizeIndex = cursor.getColumnIndex(fileSize[0]);

                // Gets the size of the image in bytes
                long imageSize = cursor.getLong(sizeIndex);

                // If the size of the image is less than 10Mb, do this:
                if(imageSize < 10e6) {

                    // Gets the path of the image selected and prints it to the log
                    imgPath = cursor.getString(columnIndex);
                    Log.d("path", imgPath);

                    cursor.close();

                    // Accesses shared preferences file
                    SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = mPreferences.edit();

                    // Stores the wallpaper path within shared preferences
                    editor.putString("wallpaper_path", imgPath);
                    editor.apply();

                    // Retrieves position of item clicked in shared preferences
                    Integer position = mPreferences.getInt("temp_wifi_position", 0);

                    // Retrieves list of WifiItems
                    ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");

                    // Sets the icon of the WifiItem pressed
                    changeIcon(position, R.drawable.ic_home);
                    (wifiList.get(position)).changeIcon(R.drawable.ic_home);

                    // Sets the path of the wallpaper clicked on to the WifiItem pressed
                    setWallpaperPath(position, imgPath);
                    (wifiList.get(position)).setWifiWallpaperPath(imgPath);

                    // Logs WifiItem pressed for debugging/testing purposes
                    Log.d("wifiList", wifiList.get(position).toString());

                    // Saves the new modified array of WifiItems to the system
                    saveArrayList(wifiList, "wifi_array_list");

                }
                else {
                    Toast.makeText(getActivity(), "Image selected is too large (>10Mb). Please try again.", Toast.LENGTH_LONG).show();
                }


            }
            else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    private void createWifiList() {
        // Declares temporary variables for the creation of the Wi-Fi SSID list
        List<String> ssidList = new ArrayList<>();
        wifiList = new ArrayList<>();

        // Accesses the WifiManager class and gets a list of the configured networks
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredList = wifiManager.getConfiguredNetworks();

        // Goes through each SSID and removes the whitespace from them
        for(WifiConfiguration config : configuredList) {
            ssidList.add(config.SSID.replace("\"", ""));
        }

        // Creates a new WifiItem for each SSID
        for (int i = 0; i < configuredList.size(); i++){
            wifiList.add(new WifiItem(R.drawable.ic_signal_wifi, ssidList.get(i), selectedImagePath));
        }

        // Stores the list of WifiItems
        saveArrayList(wifiList, "wifi_array_list");
    }




    public void saveArrayList(ArrayList<WifiItem> list, String key){
        // Stores the WifiItems list in shared preferences as a JSON object
        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<WifiItem> getArrayList(String key){
        // Retrieves the WifiItems list in shared preferences stored as a JSON object and converts them into an ArrayList
        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WifiItem>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public void changeIcon(int position, int image) {
        // Changes the icon of the SSID clicked on
        wifiList = getArrayList("wifi_array_list");
        if (wifiList != null){
            wifiList.get(position).changeIcon(image);
            mAdapter.notifyItemChanged(position);
        }
        else {
            Log.d("WifiModeFragment", "wifiList is empty");
        }
    }

    public void setWallpaperPath(int position, String wallpaperPath) {
        // Sets the wallpaper path of the WifiItem clicked on
        ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");
        wifiList.get(position).setWifiWallpaperPath(wallpaperPath);
        mAdapter.notifyItemChanged(position);
    }

    public void onClick(View view) {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void replaceFragment(Fragment someFragment) {
        // Replaces the current fragment with the fragment passed into the method
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
