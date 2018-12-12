package com.example.ben.kameleon;

public class WifiItem {
    private int mImageResource;
    private String mWifiName;

    public WifiItem(int imageResource, String wifiName) {
        mImageResource = imageResource;
        mWifiName = wifiName;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getWifiName() {
        return mWifiName;
    }
}
