package com.example.carl.womenofinfluence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private GlobalAppData appData;
    private ImageButton featureVideo;
    private static final String TAG = "Home";

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

        //checks for notification
        checkNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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

    //checks the notification for any readable data.
    public void checkNotification() {
        VideoData video = null;
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                for ( VideoData videoData : appData.getVideoData()) {
                    if (key.toString().equals("VIDEO") && value.toString().equals(videoData.getName().replaceFirst("[.][^.]+$", ""))) {
                        video = videoData;
                    }
                }
            }
        }
        // [END handle_data_extras]

        //if video data was defined in the data payload
        if (video != null) {
            //Proceed to View_Video
            Intent intent = new Intent(this, ViewVideo.class);
            intent.putExtra("videoIndex", video);
            startActivity(intent);
        }
    }

    public void refreshContent() {
        appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), Home.this);
        featureVideo = (ImageButton) findViewById(R.id.featureVideoBtn);
        Toast.makeText(getApplicationContext(), "Feature Video Refreshed", Toast.LENGTH_SHORT).show();
    }
}
