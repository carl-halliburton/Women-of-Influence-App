package nz.co.hawkefilms.womenofinfluence;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
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

public class
ViewVideo extends AppCompatActivity {

    private GlobalAppData appData; //singleton instance of globalAppData
    private VideoData videoData; //single video data object
    private Boolean dialogIsOpen; //ensure that only one video/wifi error dialog is displayed

    private ProgressBar videoProgressBar;
    private ProgressBar sharingProgressBar;
    private LinearLayout sharingLayout;
    private LinearLayout shareFailLayout;
    private VideoView videoView;
    private Integer savedVideoPosition; //the current position of the video
    private boolean refreshed;
    private TextView sharingUrl;
    private ShareDialog shareDialogFB;
    private FileSharer fileSharer;
    private ShareVideo share;

    //Video Stats Analytics
    private FirebaseAnalytics mFirebaseAnalytics;
    private SearchView searchView;
    private Toolbar toolbar;

    private LinearLayout portraitItems;
    private boolean isPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        setTitle(R.string.app_name);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        //vid view imp onCreate code
        videoView = (VideoView) findViewById(R.id.videoView);
        portraitItems = (LinearLayout) findViewById(R.id.portraitItems);
        refreshed = false;

        share = new ShareVideo(this);
        share = new ShareVideo(this);
        shareDialogFB = new ShareDialog(this);
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


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

        //progress bar shows when video is buffering
        videoProgressBar = (ProgressBar) findViewById(R.id.videoLoadingBar);
        sharingProgressBar = (ProgressBar) findViewById(R.id.shareLoadBar);

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
                                    setSharingLink();
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

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), ViewVideo.this, "");

        TextView videoTitle;

        videoTitle = (TextView) findViewById(R.id.txtVideoTitle);
        videoTitle.setText(videoData.getName());
        toolbar.setVisibility(View.VISIBLE);
        sharingUrl = (TextView) findViewById(R.id.shareLink);
        sharingLayout = (LinearLayout) findViewById(R.id.shareMenu);
        shareFailLayout = (LinearLayout) findViewById(R.id.shareFailLayout);

        setSharingLink();

        setOrientation();

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

    @Override
    public void onResume() {
        super.onResume();
        //TODO videoview.resume() currently does nothing as the videoview is refreshed when onResume is called.
        //videoView.resume();

        //show progressbar because videoView is reloaded on onResume()
        videoProgressBar.setVisibility(View.VISIBLE);
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

//-------------------------------------------------------------------------------------------------
        //the search functionality is out of scope for the moment,
        // it will be re-enabled in a future update
        // to re-enable uncomment the searchView widget int eh menu.xml file
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

        //NOTE the search functionality is currently  out of scope
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
//-------------------------------------------------------------------------------------------------
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(ViewVideo.this, Home.class));
                return true;
            case R.id.action_app_settings:
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

    /*
	Description:
		video view imp play method
	Parameters: void
	Return Type: void
	*/
    private void PlayVideo() {
        videoProgressBar.setVisibility(View.VISIBLE);
        //to resolve see through video view issue
        videoView.setBackgroundColor(Color.BLACK);
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController;

            //define media controller behaviour based on screen orientation
            if (isPortrait) {
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
                    if (isPortrait) {
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
                    //setOrientation();
                    videoProgressBar.setVisibility(View.GONE);
                    //remove black background so that video can be seen.
                    videoView.setBackgroundColor(Color.TRANSPARENT);
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
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }
    }

    /*
	Description:
		Opens the app setting so the user can turn notifications on or off
	Parameters: void
	Return Type: void
	*/
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

    /*
	Description:
		onClick functionality relating to video sharing
	Parameters: current View
	Return Type: void
	*/
    public void shareOnClick(View v) {
        switch (v.getId()) {
            case R.id.copyBtn:
                share.copyLink(videoData.getSharingUrl());
                break;
            case R.id.shareEmail:
                share.sendEmailIntent(videoData.getSharingUrl(), videoData.getName());
                break;

            case R.id.shareFacebook:
                pauseVideo();
                share.shareWithFacebook(videoData.getSharingUrl(), shareDialogFB, videoData.getName());
                break;

            case R.id.shareGooglePlus:
                pauseVideo();
                Intent shareIntent = new PlusShare.Builder(this)
                        .setText("Ascend - Woman of Influence Video - " + videoData.getName())
                        .setType("video/mp4")
                        .setContentDeepLinkId("testID",
                                "Test Title",
                                "Test Description",
                                Uri.parse(videoData.getSharingUrl()))
                        .getIntent();
                startActivityForResult(shareIntent, 0);
                //share.shareGooglePlus(setUpSharingLink(), videoData.getName());
                break;

            case R.id.shareTwitter:
                share.shareWithTwitter(videoData.getSharingUrl(), videoData.getName());
                break;

            case R.id.shareHangouts:
                pauseVideo();
                share.shareWithHangouts(videoData.getSharingUrl(), videoData.getName());
                break;

            case R.id.shareWhatsApp:
                pauseVideo();
                share.shareWithWhatsApp(videoData.getSharingUrl(), videoData.getName());
                break;

            case R.id.refreshSharingBtn:
                setSharingLink();
                break;
        }
    }

	/*
	Description:
		This method connects to dropbox to fetch a dropbox sharing link for the video
	Parameters: void
	Return Type: void
	*/
    private void setSharingLink() {
        sharingLayout.setVisibility(View.GONE);
        sharingProgressBar.setVisibility(View.VISIBLE);
        shareFailLayout.setVisibility(View.GONE);
        //Run on a separate thread to reduce load times.
        final Thread loadTask = new Thread() {
            public void run() {
                    fileSharer = new FileSharer(DropboxClient.getClient(getString(R.string.ACCESS_TOKEN)),
                            videoData);
                    fileSharer.execute();
                    try {
                        fileSharer.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    videoData = fileSharer.getVideoData();
                    appData.updateVideoUrl(videoData);
            }
        };

        //Loading UI Elements in this thread
        final Thread setTask = new Thread() {
            public void run() {
                if (!videoData.getSharingUrl().equals(VideoData.SHARINGFAILMESSAGE)) {
                    sharingLayout.setVisibility(View.VISIBLE);
                    sharingUrl.setText(videoData.getSharingUrl());
                } else {
                    shareFailLayout.setVisibility(View.VISIBLE);
                }
                sharingProgressBar.setVisibility(View.GONE);
            }
        };

        //start background loader in a separate thread
        final Thread startLoad = new Thread() {
            public void run() {
                loadTask.start();
                try {
                    loadTask.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(setTask);
            }

        };

        startLoad.start();
    }

    public void pauseVideo() {
        videoView.requestFocus();
        videoView.pause();
    }

    //Runs when screen orientation is changed.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setOrientation();
    }

    /*
    Description:
        Sets the width, height and visibility of views depending on the orientation of the screen.
        Note that for videoView the params
    Parameters: void
    Return Type: void
    */
    public void setOrientation(){
        LinearLayout videoViewArea = (LinearLayout) findViewById(R.id.videoViewArea);
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        View decorView = getWindow().getDecorView();

        ViewGroup.LayoutParams videoParams = videoView.getLayoutParams();

        ViewGroup.LayoutParams videoAreaParams = videoViewArea.getLayoutParams();

        TextView videoTitle;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //Check if in portrait or landscape
        if (isPortrait) {
            //in portrait view
            videoTitle = (TextView) findViewById(R.id.txtVideoTitle);
            videoTitle.setText(videoData.getName());
            toolbar.setVisibility(View.VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            portraitItems.setVisibility(View.VISIBLE);

            int videoviewHeight = (int) (getResources().getDimension(R.dimen.videoMaximumHeight)
                    / getResources().getDisplayMetrics().density);

            videoAreaParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    videoviewHeight, getResources().getDisplayMetrics());
            videoParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            videoParams.width = LinearLayout.LayoutParams.MATCH_PARENT;

            videoView.setLayoutParams(videoParams);
            videoViewArea.setLayoutParams(videoAreaParams);
            isInstalled();
        } else {
            //in landscape view
            //hide toolbar and status bar
            toolbar.setVisibility(View.GONE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            portraitItems.setVisibility(View.GONE);

            videoParams.height = displayMetrics.heightPixels;
            videoAreaParams.height = displayMetrics.heightPixels;

            videoView.setLayoutParams(videoParams);
            videoViewArea.setLayoutParams(videoAreaParams);
        }
    }

//-------------------------------------------------------------------------------------------------
    /*
    Description:
        Checks if Hangouts and WhatsApp are Installed
        hides share icon if not
        used in conjunction with isAppInstalled(String packageName)
        and isEnabled(String packageName)
    Parameters: void
    Return Type: void
    */
    public void isInstalled() {
        if (!isAppInstalled("com.whatsapp")) {
            ImageButton imgWhatsApp = (ImageButton) findViewById(R.id.shareWhatsApp);
            imgWhatsApp.setVisibility(View.GONE);
            TextView textWhatsApp = (TextView) findViewById(R.id.txtShareWhatApp);
            textWhatsApp.setVisibility(View.GONE);
        }

        if (!isEnabled("com.google.android.talk")) {
            ImageButton imgHangouts = (ImageButton) findViewById(R.id.shareHangouts);
            imgHangouts.setVisibility(View.GONE);
            TextView textHangouts = (TextView) findViewById(R.id.txtShareHangouts);
            textHangouts.setVisibility(View.GONE);
        }

        if (!isEnabled("com.google.android.apps.plus")) {
            ImageButton imgGooglePlus = (ImageButton) findViewById(R.id.shareGooglePlus);
            imgGooglePlus.setVisibility(View.GONE);
            TextView textGooglePlus = (TextView) findViewById(R.id.txtShareGooglePlus);
            textGooglePlus.setVisibility(View.GONE);
        }

    }

    /*
    Description:
        Checks if share app is enabled or disabled
    Parameters: String containing share app package name
    Return Type: Boolean, true if enabled, false if disabled
    */
    public boolean isEnabled(String packageName) {

        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(packageName,0);
            return ai.enabled;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /*
    Description:
        Checks if share app is installed
    Parameters: String containing share app package name
    Return Type: Boolean, true if installed, false if not installed
    */
    public boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}