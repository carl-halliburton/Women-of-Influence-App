package com.example.carl.womenofinfluence;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SearchResults extends AppCompatActivity {

    private Notification notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white); // inserts home icon on the left of the toolbar

        notify = new Notification(this); //manages the notification check/uncheck and
                                        // sending notifications to user
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
                startActivity(new Intent(SearchResults.this, Home.class));
                return true;
            case R.id.action_notification:
                if(item.isChecked())
                    notify.isChecked(item);
                else
                    notify.isUnChecked(item);
                return true;
            case R.id.title_activity_video_gallery:
                startActivity(new Intent(SearchResults.this, VideoGallery.class));
                return true;
            case R.id.title_activity_feedback:
                startActivity(new Intent(SearchResults.this, Feedback.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
