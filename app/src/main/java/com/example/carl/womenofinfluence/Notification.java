package com.example.carl.womenofinfluence;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

/**
 * Created by carl on 15/08/2016.
 * This class manages the notification on off as well sending notifications
 * At the moment it does not work, I have not sure how to link it with the activity layout view
 */
public class Notification {

    public Notification() {

    }
/*
    public void isChecked(MenuItem item) {
        // If item already checked then unchecked it
        item.setChecked(false);
        new AlertDialog.Builder(this)
                .setTitle("Notifications Off")
                .setMessage("Notifications have been turned off")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void isUnChecked(MenuItem item) {
        // If item is unchecked then checked it
        item.setChecked(true);

        new AlertDialog.Builder(this)
                .setTitle("Notifications On")
                .setMessage("Notifications have been turned on")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
*/
}
