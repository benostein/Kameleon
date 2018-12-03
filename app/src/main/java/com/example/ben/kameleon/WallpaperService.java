package com.example.ben.kameleon;

import android.app.Service;
import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


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
}
