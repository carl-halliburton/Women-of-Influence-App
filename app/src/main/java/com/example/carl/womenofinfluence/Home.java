package com.example.carl.womenofinfluence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Home extends AppCompatActivity {

    private GlobalAppData appData;
    private ImageButton featureVideo;

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Home.this);

        if(appData.getVideoData().size() == 0)
        {
            new AlertDialog.Builder(Home.this)
                    .setTitle(getString(R.string.server_connection_error_title))
                    .setMessage(R.string.server_connection_error)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            //set the first video in the list as the featured video
            featureVideo = (ImageButton) findViewById(R.id.featureVideoBtn);
        }

        //TODO move this notification method elsewhere
        setUpNotifications(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        //R.id.menu_refresh.setVisible(false);
        appData.getNotify().checkNotificationStatus(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                if (item.isChecked())
                    appData.getNotify().isChecked(item, this);
                else
                    appData.getNotify().isUnChecked(item, this);
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(Home.this, VideoGallery.class));
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(Home.this, Feedback.class));
                return true;
            case R.id.action_search:
                return true;
            case R.id.menu_refresh:
                refreshContent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void videoGalleryButtonOnClick(View v) {
        startActivity(new Intent(Home.this, VideoGallery.class));
    }

    public void feedbackButtonOnClick(View v) {
        startActivity(new Intent(Home.this, Feedback.class));
    }

    public void viewVideoFeaturedLink(View v) {
        if(appData.getVideoData().size() == 0)
        {
            new AlertDialog.Builder(Home.this)
                    .setTitle(getString(R.string.server_connection_error_title))
                    .setMessage(getString(R.string.server_connection_error))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            //Proceed to ViewVideo
            Intent intent = new Intent(Home.this, ViewVideo.class);
            intent.putExtra("videoIndex", appData.getVideoData().get(0));
            startActivity(intent);
        }
    }

    public void setUpNotifications(Context context) {
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(context, "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(context, "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //If play service is available
        if (checkPlayServices(context)) {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    private boolean checkPlayServices(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        //Checking play service is available or not
        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);

        //if play service is not available
        if(resultCode != ConnectionResult.SUCCESS) {
            //If play service is supported but not installed
            if(googleAPI.isUserResolvableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(context, "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(context, "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            return false;
        }

        return true;
    }

    public void refreshContent() {
        appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), Home.this);
        featureVideo = (ImageButton) findViewById(R.id.featureVideoBtn);
        Toast.makeText(getApplicationContext(), "Feature Video Refreshed", Toast.LENGTH_SHORT).show();
    }
}
