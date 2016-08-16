package com.example.carl.womenofinfluence;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class VideoGallery extends AppCompatActivity {

    private Notification notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white);
        notify = new Notification(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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
                if(item.isChecked())
                    notify.isChecked(item);
                else
                    notify.isUnChecked(item);
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
}
