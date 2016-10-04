package com.example.carl.womenofinfluence;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private GlobalAppData appData;
    private Button featureVideo;
    private boolean refreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refreshing = false;

        featureVideo = (Button) findViewById(R.id.featureVideoBtn);
        refreshContent();
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
            //case R.id.action_search:
                //return true;
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
        if (appData.getVideoData().size() == 0) {
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
        } else {
            //Proceed to ViewVideo
            Intent intent = new Intent(Home.this, ViewVideo.class);
            intent.putExtra("videoIndex", appData.getVideoData().get(0));
            startActivity(intent);
        }
    }

    public void refreshContent() {
        if (!refreshing) {
            refreshing = true;

            //progress dialog shows when videos are loading
            final ProgressDialog progressDialog = ProgressDialog.show(Home.this, "", "Loading Videos...", true);
            final Toast refreshDialog = Toast.makeText(getApplicationContext(), "Feature Video Refreshed", Toast.LENGTH_SHORT);
            final Handler mHandler = new Handler();

            //Data load is done here
            final Thread refreshTask = new Thread() {
                public void run() {
                    try {
                        if (appData == null)
                            appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Home.this);
                        else {
                            appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), Home.this);
                            refreshDialog.show();
                        }
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            //UI elements are updated in this thread
            final Thread setTask = new Thread() {
                public void run() {
                    setFeatureVideoLink();
                    progressDialog.dismiss();
                    refreshing = false;
                }
            };

            //This thread waits for refresh then updates UI with handler
            Thread waitForRefresh = new Thread() {
                public void run() {
                    try {
                        refreshTask.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.post(setTask);
                }
            };
            refreshTask.start();
            waitForRefresh.start();
        }
    }

    public void setFeatureVideoLink() {

        if (appData.getVideoData().size() == 0) {
            featureVideo.setVisibility(View.GONE);
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
        } else {
            //set the first video in the list as the featured video
            if (appData.getVideoData().size() != 0) {
                String buttonText = "Feature Video\n" + appData.getVideoData().get(0).getName().replaceFirst("[.][^.]+$", "");
                featureVideo.setText(buttonText);
                featureVideo.setVisibility(View.VISIBLE);
            }
        }
    }
}
