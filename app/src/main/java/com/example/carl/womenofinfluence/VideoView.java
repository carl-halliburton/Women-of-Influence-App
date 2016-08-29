package com.example.carl.womenofinfluence;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoView extends AppCompatActivity {

    private Singleton tempSingleton;
    private WebView myWebview;
    private Button fullScreenButton;
    private TextView titleText;
    private TextView aboutText;
    private Toolbar screenToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white);

        tempSingleton = Singleton.getInstance();

        //map the widgets to objects
        fullScreenButton = (Button) findViewById(R.id.fullScreenButton);
        titleText = (TextView) findViewById(R.id.appTitle);
        aboutText = (TextView) findViewById(R.id.aboutVidText);
        screenToolbar = (Toolbar) findViewById(R.id.toolbar);

        //implementation of WebView for the video
        myWebview = (WebView) findViewById(R.id.webView);
        myWebview.setWebViewClient(new WebViewClientSettings());

        myWebview.getSettings().setLoadsImagesAutomatically(true);
        myWebview.getSettings().setJavaScriptEnabled(true);
        myWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //should be changed later to handle other video links (Currently loads a video for testing)
        myWebview.loadUrl("https://player.vimeo.com/video/179155110?player_id=player&title=0&byline=0&portrait=0&autoplay=1&api=1");
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
                startActivity(new Intent(VideoView.this, Home.class));
                return true;
            case R.id.action_notification:
                if (item.isChecked())
                    tempSingleton.getNotify().isChecked(item, this);
                else
                    tempSingleton.getNotify().isUnChecked(item, this);
                return true;
            case R.id.title_activity_video_gallery:
                startActivity(new Intent(VideoView.this, VideoGallery.class));
                return true;
            case R.id.title_activity_feedback:
                startActivity(new Intent(VideoView.this, Feedback.class));
                return true;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //full screen onClick method (currently incomplete)
    public void onClickFullscreen( View v )
    {
        screenToolbar.setVisibility(View.GONE);
        fullScreenButton.setVisibility(View.GONE);
        titleText.setVisibility(View.GONE);
        aboutText.setVisibility(View.GONE);
        myWebview.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
    }
}
