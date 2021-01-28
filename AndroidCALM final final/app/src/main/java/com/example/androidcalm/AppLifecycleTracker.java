package com.example.androidcalm;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLifecycleTracker extends Application implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static boolean isInBackground = false;

    public static boolean getIsInBackground(){
        return isInBackground;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(isInBackground = true) {
            isInBackground = false;
            Intent intent = new Intent(this, BackgroundMusic.class);
            intent.setAction("resume");
            startService(intent);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        isInBackground = true;
        Intent intent = new Intent(this, BackgroundMusic.class);
        intent.setAction("pause");
        startService(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
