package com.example.carl.womenofinfluence;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white);

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), VideoGallery.this);

        loadGallery();
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
            case R.id.menu_video_gallery:
                startActivity(new Intent(VideoGallery.this, VideoGallery.class));
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
        List<ImageButton> galleryLinks;
        List<TextView> galleryDescriptions;
        LinearLayout galleryView;

        //create video gallery buttons
        galleryView = (LinearLayout) findViewById(R.id.gallery);
        galleryLinks = new ArrayList<>();
        galleryDescriptions = new ArrayList<>();
        int i = 0;

        for (VideoData link : appData.getVideoData()) {
            //TODO change the TYPE Button to ImageButton when images are ready to be added for each video.
            //create the button for the video link
            galleryLinks.add(new ImageButton(this));

            //set Thumbnail
            galleryLinks.get(i).setBackground( getResources().getDrawable(R.drawable.video_place_holder));

            galleryLinks.get(i).setId(i);
            //set button size
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
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

            //create the TextViews for the video descriptions
            //TODO add the description String to VideoData class and set the text here.
            galleryDescriptions.add(new TextView(this));
            galleryDescriptions.get(i).setText(link.getName().replaceFirst("[.][^.]+$", ""));
            galleryDescriptions.get(i).setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            galleryDescriptions.get(i).setGravity(Gravity.CENTER);
            galleryView.addView(galleryDescriptions.get(i));
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
        appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), VideoGallery.this);
        loadGallery();
        Toast.makeText(getApplicationContext(), "Gallery refreshed", Toast.LENGTH_SHORT).show();
    }
}
