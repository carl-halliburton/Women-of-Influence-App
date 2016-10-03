package com.example.carl.womenofinfluence;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class ViewVideo extends AppCompatActivity {

    private GlobalAppData appData;
    private VideoData videoData;
    private Boolean dialogIsOpen;

    private static ProgressDialog progressDialog;
    private VideoView videoView;
    private TextView videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        //vid view imp onCreate code
        videoView = (VideoView) findViewById(R.id.videoView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            videoData = (VideoData) extras.getSerializable("videoIndex");
            //videoTitle.setText(videoData.getName()); //null pointer exception has been handled with the precious if statement
        }
        dialogIsOpen = false;

        //progress dialog shows when video is buffering
        progressDialog = ProgressDialog.show(ViewVideo.this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);

        //set up lister to handle VideoView errors
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (!dialogIsOpen) {
                    dialogIsOpen = true;
                    new AlertDialog.Builder(ViewVideo.this)
                            .setTitle("Video can't be played")
                            .setMessage("Please check your connection and reload video")
                            .setPositiveButton("Reload Video", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Reload ViewVideo
                                    dialogIsOpen = false;
                                    PlayVideo();
                                }
                            })
                            .setNegativeButton("Return to Gallery", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialogIsOpen = false;
                                    Intent intent1 = new Intent(ViewVideo.this, VideoGallery.class);
                                    startActivity(intent1);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
                return true;
            }
        });


        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), ViewVideo.this);

        //Check if in portrait or landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            videoTitle = (TextView) findViewById(R.id.txtVideoTitle);
            videoTitle.setText(videoData.getName());
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        PlayVideo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        item = menu.findItem(R.id.menu_refresh);
        item.setVisible(false);

        //appData.getNotify().checkNotificationStatus(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(ViewVideo.this, Home.class));
                return true;
            case R.id.action_notification:
                if (item.isChecked())
                    appData.getNotify().isChecked(item, this);
                else
                    appData.getNotify().isUnChecked(item, this);
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(ViewVideo.this, VideoGallery.class));
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(ViewVideo.this, Feedback.class));
                return true;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //vid view imp play method
    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(ViewVideo.this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videoData.getTempUrl());
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }

    }
}
