package nz.co.hawkefilms.womenofinfluence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Description:
 * The home activity although it is not the first activity run - see SplashScreen.java
 * Introduces the app and displays the feature video
 */

public class Home extends AppCompatActivity {

    private GlobalAppData appData; //singleton instance of globalAppData
    private Button featureVideo;
    private boolean refreshing;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        refreshing = false;

        featureVideo = (Button) findViewById(R.id.btnFeatureVideo);
        refreshContent();
    }

  /*  //refreshes feature video automatically if required when resuming the Home screen
    @Override
    protected void onResume(){
        super.onResume();
        if (appData.getVideoData().size() == 0) {
            refreshContent();
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if(focusedView != null)
            inm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

//-------------------------------------------------------------------------------------------------
        //the search functionality is out of scope for the moment,
        // it will be re-enabled in a future update
        // to re-enable uncomment the searchView widget int eh menu.xml file
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //the search functionality is out of scope for the moment,
        // it will be re-enabled in a future update
        menu.findItem(R.id.search).setVisible(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Proceed to Search Results
                Intent intent = new Intent(Home.this, SearchResults.class);
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
                InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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
            case R.id.action_app_settings:
                openAppSettings();
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(Home.this, VideoGallery.class));
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(Home.this, Feedback.class));
                return true;
            case R.id.menu_refresh:
                refreshContent();
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
       if (appData.getVideoData().size() == 0) {
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
        } else {
            //Proceed to ViewVideo
            Intent intent = new Intent(Home.this, ViewVideo.class);
            intent.putExtra("videoIndex", appData.getVideoData().get(0));
            startActivity(intent);
        }
    }

    /*
    Description:
        This method handles the refreshing of content in other threads while displaying
        a progress dialog in the current thread.
    Parameters: void
    Return Type: void
    */
    public void refreshContent() {
        if (!refreshing) {
            refreshing = true;

            //progress dialog shows when videos are loading
            final ProgressDialog progressDialog = ProgressDialog.show(Home.this, "", "Loading Videos...", true);
            final Toast refreshDialog = Toast.makeText(getApplicationContext(), "Feature Video Refreshed", Toast.LENGTH_SHORT);

            //Data load is done here
            final Thread refreshTask = new Thread() {
                public void run() {
                    try {
                        if (appData == null)
                            appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Home.this, "");
                        else {
                            appData.refreshDropboxFiles(getString(R.string.ACCESS_TOKEN), Home.this, "");
                            refreshDialog.show();
                        }
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            //UI elements are updated in this thread
            final Thread setTask = new Thread() {
                public void run() {
                    setFeatureVideoLink();
                    progressDialog.dismiss();
                    refreshing = false;
                }
            };

            //This thread waits for refresh then updates UI with handler
            Thread waitForRefresh = new Thread() {
                public void run() {
                    try {
                        refreshTask.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(setTask);
                }
            };
            refreshTask.start();
            waitForRefresh.start();
        }
    }

    /*
    Description:
        sets video link fo feature video
    Parameters: void
    Return Type: void
    */
    public void setFeatureVideoLink() {

        if (appData.getVideoData().size() == 0) {
            featureVideo.setVisibility(View.GONE);
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
        } else {
            //set the first video in the list as the featured video
            if (appData.getVideoData().size() != 0) {
                String buttonText = "Feature Video\n" + appData.getVideoData().get(0).getName().replaceFirst("[.][^.]+$", "");
                featureVideo.setText(buttonText);
                featureVideo.setVisibility(View.VISIBLE);
            }
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

        } catch ( ActivityNotFoundException e ) {
            new AlertDialog.Builder(Home.this)
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
}
