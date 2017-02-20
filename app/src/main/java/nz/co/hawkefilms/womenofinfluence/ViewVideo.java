package nz.co.hawkefilms.womenofinfluence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.VideoView;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.ExecutionException;

/**
 * Description: This is the video player, it manages the playing of the video and all asociated
 * tasks required in
 * the videoView activity.
 */

public class ViewVideo extends AppCompatActivity {

    private GlobalAppData appData; //singleton instance of globalAppData
    private VideoData videoData; //single video data object
    private Boolean dialogIsOpen; //ensure that only one video/wifi error dialog is displayed

    private static ProgressDialog progressDialog;
    private VideoView videoView;
    private Integer savedVideoPosition; //the current position of the video
    private boolean refreshed;
    private boolean portraitView;
    private TextView sharingUrl;
    private ShareDialog shareDialogFB;
    private FileSharer fileSharer;
    private ShareVideo share;

    //Video Stats Analytics
    private FirebaseAnalytics mFirebaseAnalytics;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        setTitle(R.string.app_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        //vid view imp onCreate code
        videoView = (VideoView) findViewById(R.id.videoView);
        refreshed = false;

        share = new ShareVideo(this);
        share = new ShareVideo(this);
        shareDialogFB = new ShareDialog(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoData = (VideoData) extras.getSerializable("videoIndex");
        }

        //check if activity refreshed
        if (savedInstanceState != null) {
            savedVideoPosition = savedInstanceState.getInt("VideoTime");
            refreshed = true;
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
                                    if (portraitView) {
                                        sharingUrl = (TextView) findViewById(R.id.shareLink);
                                        sharingUrl.setText(setUpSharingLink());
                                    }
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

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN),
                ViewVideo.this, "");

        portraitView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        TextView videoTitle;
        //Check if in portrait or landscape
        if (portraitView) {
            videoTitle = (TextView) findViewById(R.id.txtVideoTitle);
            videoTitle.setText(videoData.getName());
            toolbar.setVisibility(View.VISIBLE);
            sharingUrl = (TextView) findViewById(R.id.shareLink);
            sharingUrl.setText(setUpSharingLink());
        } else {
            View decorView = getWindow().getDecorView();

            //hide toolbar and status bar
            toolbar.setVisibility(View.GONE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        PlayVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if(focusedView != null)
            inm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }


    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate if the process is
    // killed and restarted.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //save the current position of the video, used when orientation changes
        savedInstanceState.putInt("VideoTime", videoView.getCurrentPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        //Hide refresh menu item as not required in this activity
        item = menu.findItem(R.id.menu_refresh);
        item.setVisible(false);

        //the search functionality is out of scope for the moment,
        // it will be re-enabled in a future update
        menu.findItem(R.id.search).setVisible(false);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Proceed to Search Results
                Intent intent = new Intent(ViewVideo.this, SearchResults.class);
                intent.putExtra("searchInput", searchView.getQuery().toString());
                searchView.clearFocus();
                startActivity(intent);
                return false;
            }
        });

        //customise the search view
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search_black);
        searchView.setIconifiedByDefault(false);

        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                searchView.setIconifiedByDefault(false);
                searchView.setFocusable(true);
                searchView.requestFocusFromTouch();
                return true;
            }
        });

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
                openAppSettings();
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(ViewVideo.this, VideoGallery.class));
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(ViewVideo.this, Feedback.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //video view imp play method
    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController;

            //define media controller behaviour based on screen orientation
            if (portraitView) {
                mediaController = new MediaController(this) {
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction()
                                == KeyEvent.ACTION_UP)
                            ((Activity) getContext()).finish();

                        return super.dispatchKeyEvent(event);
                    }

                    @Override
                    public void show() {
                        super.show(5000);
                    }
                };
            } else { //if in landscape view
                mediaController = new MediaController(ViewVideo.this) {
                    //hide after 5 seconds
                    @Override
                    public void show() {
                        super.show(5000);
                    }
                };
            }

            //set up videoView
            mediaController.setAnchorView(videoView);

            final Uri video = Uri.parse(videoData.getTempUrl());
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    //if refreshed continue from last know position
                    if (savedVideoPosition != null && refreshed) {
                        videoView.seekTo(savedVideoPosition);
                        refreshed = false;
                    }

                    //Simulates the onTouchEvent to show the Media controller
                    if (portraitView) {
                        videoView.dispatchTouchEvent(MotionEvent.obtain(
                                SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis() + 100,
                                MotionEvent.ACTION_DOWN,
                                0.0f,
                                0.0f,
                                0
                                )
                        );
                    }

                    progressDialog.dismiss();
                    videoView.start();

                    // Obtain the FirebaseAnalytics instance.
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

                    //log when the video starts
                    Bundle params = new Bundle();
                    params.putDouble(FirebaseAnalytics.Param.VALUE, 1.0);
                    mFirebaseAnalytics.logEvent(videoData.getVideoStatsName(), params);
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }
    }

    //Opens the app setting so the user can turn notifications on or off
    public void openAppSettings() {
        String packageName = getString(R.string.package_name);

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(ViewVideo.this)
                    .setTitle("Notification Settings Not Available")
                    .setMessage("Unable to open the apps settings screen, please try again later")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void shareOnClick(View v) {
        switch (v.getId()) {
            case R.id.copyBtn:
                share.copyLink(setUpSharingLink());
                break;
            case R.id.shareEmail:
                share.sendEmailIntent(setUpSharingLink(), videoData.getName());
                break;

            case R.id.shareFacebook:
                share.shareWithFacebook(setUpSharingLink(), shareDialogFB, videoData.getName());
                break;

            //case R.id.shareMessanger:
                //break;

            case R.id.shareGooglePlus:
                Intent shareIntent = new PlusShare.Builder(this)
                        .setText("Ascend - Woman of Influence Video - " + videoData.getName())
                        .setType("video/mp4")
                        .setContentDeepLinkId("testID",
                                "Test Title",
                                "Test Description",
                                Uri.parse(setUpSharingLink()))
                        .getIntent();
                startActivityForResult(shareIntent, 0);
                //share.shareGooglePlus(setUpSharingLink(), videoData.getName());
                break;

            case R.id.shareTwitter:
                share.shareWithTwitter(setUpSharingLink(), videoData.getName());
        }
    }

    private String setUpSharingLink() {
        if (videoData.getSharingUrl().equals("Error: cannot find url")) {
            fileSharer = new FileSharer(DropboxClient.getClient(getString(R.string.ACCESS_TOKEN)),
                    videoData);
            fileSharer.execute();
            try {
                fileSharer.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            videoData = fileSharer.getVideoData();
            appData.updateVideoUrl(videoData);
        }
        return videoData.getSharingUrl();
    }


}