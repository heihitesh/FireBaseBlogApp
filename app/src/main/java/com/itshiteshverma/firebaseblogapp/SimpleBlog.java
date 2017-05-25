package com.itshiteshverma.firebaseblogapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Wilmar Africa Ltd on 24-05-17.
 */

public class SimpleBlog extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            //it enable the offline capabality of the firebase
        }
    }
}


