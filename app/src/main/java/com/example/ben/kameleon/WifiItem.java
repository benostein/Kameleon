package com.example.ben.kameleon;

import android.graphics.Bitmap;

public class WifiItem {
    private int mIconResource;
    private String mWifiName;
    private Bitmap mWifiWallpaper;

    public WifiItem(int imageResource, String wifiName, Bitmap wallpaper) {
        mIconResource = imageResource;
        mWifiName = wifiName;
        mWifiWallpaper = wallpaper;
    }

    public void changeIcon(int image) {
        mIconResource = image;
    }

    public void setWifiWallpaper(Bitmap wallpaper) {
        mWifiWallpaper = wallpaper;
    }

    public Bitmap getWifiWallpaper() {
        return mWifiWallpaper;
    }

    public int getImageResource() {
        return mIconResource;
    }

    public String getWifiName() {
        return mWifiName;
    }
}
