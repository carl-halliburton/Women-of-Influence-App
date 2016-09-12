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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VideoGallery extends AppCompatActivity {

    private Singleton tempSingleton;
    private List<Button> galleryLinks;
    private List<TextView> galleryDescriptions;
    private LinearLayout galleryView;
    private FileLister fileLister;
    private List<VideoData> videoDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white);

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

        //create video gallery buttons
        galleryView = (LinearLayout) findViewById(R.id.gallery);
        galleryLinks = new ArrayList<Button>();
        galleryDescriptions = new ArrayList<TextView>();
        int i = 0;

        for (VideoData link : videoDatas) {
            //TODO change the TYPE Button to ImageButton when images are ready to be added for each video.
            //create the button for the video link
            galleryLinks.add(new Button(this));
            galleryLinks.get(i).setText(link.getName());
            galleryLinks.get(i).setId(i);
            galleryLinks.get(i).setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            galleryView.addView(galleryLinks.get(i));

            //set the link for the video button
            galleryLinks.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Proceed to View_Video
                    Intent intent = new Intent(VideoGallery.this, ViewVideo.class);
                    intent.putExtra("videoIndex", videoDatas.get(view.getId()));
                    startActivity(intent);
                }
            });
            //create the TextViews for the video descriptions
            //TODO add the description String to VideoData class and set the text here.
            galleryDescriptions.add(new TextView(this));
            galleryDescriptions.get(i).setText("Placeholder video description for " +
                    link.getName());
            galleryDescriptions.get(i).setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            galleryView.addView(galleryDescriptions.get(i));
            i++;
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
            case android.R.id.home:
                startActivity(new Intent(VideoGallery.this, Home.class));
                return true;
            case R.id.action_notification:
                if (item.isChecked())
                    tempSingleton.getNotify().isChecked(item, this);
                else
                    tempSingleton.getNotify().isUnChecked(item, this);
                return true;
            case R.id.title_activity_video_gallery:
                startActivity(new Intent(VideoGallery.this, VideoGallery.class));
                return true;
            case R.id.title_activity_feedback:
                startActivity(new Intent(VideoGallery.this, Feedback.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewVideoLink(View v) {
        startActivity(new Intent(VideoGallery.this, VideoView.class));
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
