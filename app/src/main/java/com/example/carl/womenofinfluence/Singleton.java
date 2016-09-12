package com.example.carl.womenofinfluence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.v2.users.FullAccount;

/**
 * Created by carl on 25/08/2016.
 */
// File Name: Singleton.java
public class Singleton {

    private static Singleton instance = null;
    private Notification notify;
    private Boolean notificationStatus = false;

    private Singleton() {
        notify = new Notification();

    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public Notification getNotify() {
        return notify;
    }

    public boolean getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(Boolean status) {
        notificationStatus = status;
    }
}