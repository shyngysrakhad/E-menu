package com.a4devspirit.a1.data;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class FirebaseOfflineCapabilities extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
