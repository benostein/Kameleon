package com.example.ben.kameleon;

import android.graphics.Bitmap;

public class WifiItem {
    private int mIconResource;
    private String mWifiName;
    private String mWifiWallpaperPath;

    public WifiItem(int imageResource, String wifiName, String wallpaperPath) {
        mIconResource = imageResource;
        mWifiName = wifiName;
        mWifiWallpaperPath = wallpaperPath;
    }

    public void changeIcon(int image) {
        mIconResource = image;
    }

    public void setWifiWallpaperPath(String wallpaperPath) {
        mWifiWallpaperPath = wallpaperPath;
    }

    public String getWifiWallpaper() {
        return mWifiWallpaperPath;
    }

    public void changeImage(int image) {
        mIconResource = image;
    }

    public int getImageResource() {
        return mIconResource;
    }

    public String getWifiName() {
        return mWifiName;
    }
}
