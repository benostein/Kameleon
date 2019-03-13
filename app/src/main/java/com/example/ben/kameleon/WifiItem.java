package com.example.ben.kameleon;

public class WifiItem {
    private int mIconResource;
    private String mWifiName;
    private String mWifiWallpaperPath;

    public WifiItem(int imageResource, String wifiName, String wallpaperPath) {
        // Instantiating method, assigning each parameter to its variable name
        mIconResource = imageResource;
        mWifiName = wifiName;
        mWifiWallpaperPath = wallpaperPath;
    }

    // Changes the icon of the SSID
    public void changeIcon(int image) {
        mIconResource = image;
    }

    // Sets the wallpaper path of the SSID
    public void setWifiWallpaperPath(String wallpaperPath) {
        mWifiWallpaperPath = wallpaperPath;
    }

    // Gets the path of the wallpaper image
    public String getWifiWallpaper() {
        return mWifiWallpaperPath;
    }

    // Gets the resource used for the icon
    public int getImageResource() {
        return mIconResource;
    }

    // Gets the name of the SSID
    public String getWifiName() {
        return mWifiName;
    }
}
