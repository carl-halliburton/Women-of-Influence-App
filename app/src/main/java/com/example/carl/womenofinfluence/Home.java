package com.example.carl.womenofinfluence;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Home extends AppCompatActivity {

    private Singleton tempSingleton;
    private FileLister fileLister;
    private List<VideoData> videoDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tempSingleton = Singleton.getInstance();

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

        if (getString(R.string.ACCESS_TOKEN).equals("ACCESS_TOKEN")) {
            new AlertDialog.Builder(Home.this)
                    .setTitle("ACCESS TOKEN NOT SET")
                    .setMessage("Invalid access token detected. Please use a valid token in the" +
                            " strings.xml file. You will not be able to recieve videos until this is" +
                            " resolved. If you are a user please contact the developers.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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

    public void viewVideoLink(View v) {
        startActivity(new Intent(Home.this, ViewVideo.class));
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
