package com.example.ben.kameleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

//        List<String> wifiArrayList = loadWifiArrayList();

        ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");

        if (wifiList == null) {
            createWifiList();
        }

        //buildRecyclerView();

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        // Change if recycler view changes in size
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WifiAdapter(wifiList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, RESULT_OK);

//                try (FileOutputStream out = new FileOutputStream(file)) {
//                    selectedImagePath.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                    // PNG is a lossless format, the compression factor (100) is ignored
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = mPreferences.edit();
                //String wallpaperPath = mPreferences.getString("wallpaper_path","/storage/emulated/0/DCIM/Camera/IMG_20181219_191058.jpg");

                editor.putInt("temp_wifi_position", position);
                editor.apply();

                loadImageFromGallery();



//                wifiList.get(position).getWifiWallpaper();

//                WallpaperManager myWallpaperManager
//                        = WallpaperManager.getInstance(getActivity().getApplicationContext());
//
//                if (wallpaperPath != null) {
//                    try {
//                        myWallpaperManager.setBitmap(BitmapFactory.decodeFile(wallpaperPath));
//                        Log.d("OnClick", "Wallpaper set");
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
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
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.MediaColumns.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                Log.d("path", imgPath);
                cursor.close();

                // Creates a new shared preferences file that allows user preferences to be stored within the application
                SharedPreferences mPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                // Allows preferences file to be edited using 'editor'
                final SharedPreferences.Editor editor = mPreferences.edit();

                editor.putString("wallpaper_path", imgPath);
                editor.apply();

                Integer position = mPreferences.getInt("temp_wifi_position",0);

                ArrayList<WifiItem> wifiList = getArrayList("wifi_array_list");

                changeIcon(position, R.drawable.ic_home);

                setWallpaperPath(position, imgPath);


                (wifiList.get(position)).setWifiWallpaperPath(imgPath);

                Log.d("wifiList", wifiList.get(position).toString());

                // String[] wifiStringList = wifiList.toArray(new String[0]);


                // TODO TRY THIS: https://stackoverflow.com/a/47155338/5565085

                // saveWifiArrayList();

                saveArrayList(wifiList, "wifi_array_list");


                // Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);


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
        List<String> ssidList = new ArrayList<>();
        wifiList = new ArrayList<>();

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> configuredList = wifiManager.getConfiguredNetworks();

        for(WifiConfiguration config : configuredList) {
            ssidList.add(config.SSID.replace("\"", ""));
        }

        for (int i = 0; i < configuredList.size(); i++){
            wifiList.add(new WifiItem(R.drawable.ic_signal_wifi, ssidList.get(i), selectedImagePath));
        }

        // saveWifiArrayList();

        saveArrayList(wifiList, "wifi_array_list");
    }

//    private List<String> loadWifiArrayList() {
//        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = mPreferences.edit();
//
//        //ArrayList<WifiItem> wifiList;
//
//        List<String> wifiArrayList;
//
//        //Retrieve the Wi-Fi array list
//        Gson gson = new Gson();
//        String jsonText = mPreferences.getString("wifi_array_list", null);
////        String[] wifiArrayList = gson.fromJson(jsonText, new TypeToken<String[]>() {
////        }.getType());
//
//        Type type = new TypeToken<List<String>>() {
//        }.getType();
//        wifiArrayList = gson.fromJson(jsonText, type);
//
//        return wifiArrayList;
//    }
//
//    private void saveWifiArrayList() {
//        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = mPreferences.edit();
//
//
////        //Set the values
////        Gson gson = new Gson();
////        String jsonText = gson.toJson(wifiList);
////        editor.putString("wifi_array_list", jsonText);
////        editor.apply();
//
//    }

    public void saveArrayList(ArrayList<WifiItem> list, String key){
        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<WifiItem> getArrayList(String key){
        SharedPreferences mPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WifiItem>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public void changeIcon(int position, int image) {
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
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
