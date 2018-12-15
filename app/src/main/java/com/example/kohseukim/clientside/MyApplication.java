package com.example.kohseukim.clientside;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    public static BackEnd backend;
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}