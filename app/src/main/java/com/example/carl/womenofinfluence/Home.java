package com.example.carl.womenofinfluence;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Home extends AppCompatActivity {

    private Singleton tempSingleton;
    private FileLister fileLister;
    private List<VideoData> videoDatas;
    private Button featureVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tempSingleton = Singleton.getInstance();

        if (getString(R.string.ACCESS_TOKEN).equals("ACCESS_TOKEN")) {
            new AlertDialog.Builder(Home.this)
                    .setTitle(getString(R.string.access_token_error_title))
                    .setMessage(getString(R.string.access_token_error_description))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            fileLister = new FileLister(DropboxClient.getClient(getString(R.string.ACCESS_TOKEN)),
                    getApplicationContext());
            videoDatas = new ArrayList<VideoData>();
            fileLister.execute();
            try {
                fileLister.get(10000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            videoDatas = fileLister.getVideoDatas();

            if(videoDatas.size() == 0)
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
                featureVideo = (Button) findViewById(R.id.featureVideoBtn);
                featureVideo.setText(videoDatas.get(0).getName());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        tempSingleton.getNotify().checkNotificationStatus(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_notification:
                if (item.isChecked())
                    tempSingleton.getNotify().isChecked(item, this);
                else
                    tempSingleton.getNotify().isUnChecked(item, this);
                return true;
            case R.id.title_activity_video_gallery:
                startActivity(new Intent(Home.this, VideoGallery.class));
                return true;
            case R.id.title_activity_feedback:
                startActivity(new Intent(Home.this, Feedback.class));
                return true;
            case R.id.action_search:
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
        if(videoDatas.size() == 0)
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
            intent.putExtra("videoIndex", videoDatas.get(0));
            startActivity(intent);
        }
    }

    //TODO refreshes dropbox files in the background. However the new info would still need to be retrieved with setVideoData()
    public void refreshDropboxFiles(){
        fileLister.execute();
        setVideoData();
    }

    //TODO should be set after fileLister.execute();. since .excute runs in the background it will need to be implemented to wait for .execute
    public void setVideoData(){
        videoDatas = fileLister.getVideoDatas();
    }
}
