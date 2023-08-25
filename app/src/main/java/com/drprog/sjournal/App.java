package com.drprog.sjournal;

import android.app.Application;

public class App extends Application {

    public static App sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }
}
