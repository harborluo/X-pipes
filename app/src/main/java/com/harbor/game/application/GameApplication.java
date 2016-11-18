package com.harbor.game.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by harbor on 11/18/2016.
 */
public class GameApplication extends Application {

    private String TAG= this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        Log.d(TAG, "onCreate : game application started.");
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

}
