package com.example.kohseukim.clientside;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static BackEnd backend;

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}