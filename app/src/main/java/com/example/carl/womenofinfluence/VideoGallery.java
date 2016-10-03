package com.example.carl.womenofinfluence;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VideoGallery extends AppCompatActivity {

    private GlobalAppData appData;
    private boolean refreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);
        refreshing = false;

        refreshContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.menu_video_gallery);
        item.setVisible(false);

        appData.getNotify().checkNotificationStatus(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(VideoGallery.this, Home.class));
                return true;
            case R.id.action_notification:
                if (item.isChecked())
                    appData.getNotify().isChecked(item, this);
                else
                    appData.getNotify().isUnChecked(item, this);
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(VideoGallery.this, Feedback.class));
                return true;
            case R.id.menu_refresh:
                refreshContent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadGallery() {
        List<Button> galleryLinks;
        LinearLayout galleryView;

        //create video gallery buttons
        galleryView = (LinearLayout) findViewById(R.id.gallery);
        galleryLinks = new ArrayList<>();
        int i = 0;

        for (VideoData link : appData.getVideoData()) {
            //create the button for the video link
            galleryLinks.add(new Button(this));

            String buttonText = link.getName();
            galleryLinks.get(i).setText(buttonText);
            galleryLinks.get(i).setId(i);
            //galleryLinks.get(i).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            //set button size
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(20, 0, 0, 0);
            galleryLinks.get(i).setLayoutParams(layoutParams);

            galleryView.addView(galleryLinks.get(i));

            //set the link for the video button
            galleryLinks.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Proceed to View_Video
                    Intent intent = new Intent(VideoGallery.this, ViewVideo.class);
                    intent.putExtra("videoIndex", appData.getVideoData().get(view.getId()));
                    startActivity(intent);
                }
            });
            i++;
        }

        //if there are no videos on server
        if(appData.getVideoData().size() == 0)
        {
            new AlertDialog.Builder(VideoGallery.this)
                    .setTitle(getString(R.string.server_connection_error_title))
                    .setMessage(R.string.server_connection_error)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void refreshContent() {
        if (!refreshing) {
            refreshing = true;
            //progress dialog shows when videos are loading
            final ProgressDialog progressDialog = ProgressDialog.show(VideoGallery.this, "", "Loading Videos...", true);
            final Toast refreshDialog = Toast.makeText(getApplicationContext(), "Gallery refreshed", Toast.LENGTH_SHORT);
            final Handler mHandler = new Handler();

            //Data load is done here
            final Thread refreshTask = new Thread() {
                public void run() {
                    try {
                        if (appData == null)
                            appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), VideoGallery.this);
                        else {
                            appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), VideoGallery.this);
                            refreshDialog.show();
                        }
                        sleep(100);
                    } catch (InterruptedException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            };

            //Loading UI Elements in this thread
            final Thread setTask = new Thread() {
                public void run() {
                    loadGallery();
                    progressDialog.dismiss();
                    refreshing = false;
                }
            };

            //This thread waits for refresh then loads ui elements in handler.
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
}
