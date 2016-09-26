package com.example.carl.womenofinfluence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by carl on 15/08/2016.
 * This class manages the notification on off as well sending notifications
 * At the moment it does not work, I have not sure how to link it with the activity layout view
 */
public class Notification {

    private static Boolean status = false;

    public Notification() {
    }

    public void isChecked(MenuItem item, Context context) {
        // If item already checked then unchecked it
        item.setChecked(false);

        status = false;
        new AlertDialog.Builder(context)
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

    public void isUnChecked(MenuItem item, Context context) {
        // If item is unchecked then checked it
        item.setChecked(true);
        status = true;

        new AlertDialog.Builder(context)
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

    public boolean getStatus() {
        return status;
    }

    public void checkBox(MenuItem item, Context context) {
        item.setChecked(true);
    }

    public void unCheckBox(MenuItem item, Context context) {
        item.setChecked(false);
    }

    public void checkNotificationStatus(Menu menu) {
        if (status == true) {
            MenuItem item = menu.findItem(R.id.action_notification);
            item.setChecked(true);
        }
        else  if (status == false) {
            MenuItem item = menu.findItem(R.id.action_notification);
            item.setChecked(false);
        }
    }
}

