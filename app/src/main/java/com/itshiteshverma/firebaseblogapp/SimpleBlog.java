package com.itshiteshverma.firebaseblogapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Wilmar Africa Ltd on 24-05-17.
 */

public class SimpleBlog extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            //it enable the offline capabality of the firebase
        }
    }
}


