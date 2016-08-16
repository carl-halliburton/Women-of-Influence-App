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
import android.widget.CheckBox;

public class Home extends AppCompatActivity {

    //private Notification notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //notify = new Notification();
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
            case R.id.action_notification:
                if(item.isChecked())
                    isChecked(item);
                else
                    isUnChecked(item);
                return true;
            case R.id.title_activity_video_gallery:
                startActivity(new Intent(Home.this, VideoGallery.class));
                return true;
            case R.id.title_activity_feedback:
                startActivity(new Intent(Home.this, Feedback.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void isChecked(MenuItem item) {
        // If item already checked then unchecked it
        item.setChecked(false);
        new AlertDialog.Builder(this)
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

    public void isUnChecked(MenuItem item) {
        // If item is unchecked then checked it
        item.setChecked(true);

        new AlertDialog.Builder(this)
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

    public void videoGalleryButtonOnClick(View v) {
        startActivity(new Intent(Home.this, VideoGallery.class));
    }

    public void feedbackButtonOnClick(View v) {
        startActivity(new Intent(Home.this, Feedback.class));
    }
}
