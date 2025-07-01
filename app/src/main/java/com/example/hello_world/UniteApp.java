package com.example.hello_world;

import android.app.Application;

import com.example.hello_world.Database.DBManager;

public class UniteApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.initDB(getApplicationContext());
    }
}
