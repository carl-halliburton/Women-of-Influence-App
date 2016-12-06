package nz.co.hawkefilms.womenofinfluence;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchResults extends AppCompatActivity {

    private GlobalAppData appData;
    private boolean refreshing;
    private Button loadMore;
    private FileLister searchFileLister;
    private List<VideoData> videoInfoResults;
    private List<Metadata> dropboxSearchData; //data for loading remaining dropbox videos
    private String searchInput;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        setTitle(R.string.app_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        TextView resultsDescription = (TextView) findViewById(R.id.resultsDescription);
        searchInput = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            searchInput = extras.getString("searchInput");
        }
        resultsDescription.setText("Search results for '" + searchInput + "'");

        //load more button
        loadMore = (Button) findViewById(R.id.loadMoreBtn);

        refreshContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.menu_refresh);
        item.setVisible(false);

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
                Intent intent = new Intent(SearchResults.this, SearchResults.class);
                intent.putExtra("searchInput", searchView.getQuery().toString());
                startActivity(intent);
                return false;
            }

        });

        //customise the search view
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search_black);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
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
                openAppSettings();
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(SearchResults.this, VideoGallery.class));
                return true;
            case R.id.menu_feedback:
                startActivity(new Intent(SearchResults.this, Feedback.class));
                return true;
            case R.id.menu_refresh:
                refreshContent();
                return true;
            case R.id.search:
                //open the keyboard and set focus to searchView EditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                searchView.setIconifiedByDefault(false);
                searchView.setFocusable(true);
                searchView.requestFocusFromTouch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Opens the app setting so the user can turn notifications on or off
    public void openAppSettings() {
        String packageName = getString(R.string.package_name);

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            new AlertDialog.Builder(SearchResults.this)
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

    //This method loads the buttons for the Video Gallery after video data is found.
    public void loadResults() {
        List<Button> resultLinks;
        LinearLayout resultsView;

        //create video gallery buttons
        resultsView = (LinearLayout) findViewById(R.id.resultsView);
        //clears the linearlayout for the video buttons
        resultsView.removeAllViews();
        resultLinks = new ArrayList<>();
        int i = 0;

        //if there are no more videos left to load.
        if(searchFileLister.remainingLoads() == 0)
        {
            loadMore.setVisibility(View.GONE);
        } else {
            loadMore.setVisibility(View.VISIBLE);
        }

        for (VideoData link : videoInfoResults) {
            //create the button for the video link
            resultLinks.add(new Button(this));

            String buttonText = link.getName();
            resultLinks.get(i).setText(buttonText);
            resultLinks.get(i).setId(i);

            //set button size
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, 0, 20);
            resultLinks.get(i).setLayoutParams(layoutParams);

            resultsView.addView(resultLinks.get(i));

            //set the link for the video button
            resultLinks.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Proceed to View_Video
                    Intent intent = new Intent(SearchResults.this, ViewVideo.class);
                    intent.putExtra("videoIndex", videoInfoResults.get(view.getId()));
                    startActivity(intent);
                }
            });
            i++;
        }

        //if Dropbox connection has failed.
        if(!searchFileLister.dbConnectionSuccessfull())
        {
            new AlertDialog.Builder(SearchResults.this)
                    .setTitle(getString(R.string.server_connection_error_title))
                    .setMessage(R.string.server_connection_error)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /*Checks Dropbox for videos in another thread and shows a progress dialog in the main thread.*/
    public void refreshContent() {
        if (!refreshing) {
            refreshing = true;
            //progress dialog shows when videos are loading
            final ProgressDialog progressDialog = ProgressDialog.show(SearchResults.this, "", "Loading Videos...", true);
            final Toast refreshDialog = Toast.makeText(getApplicationContext(), "Results refreshed", Toast.LENGTH_SHORT);
            final Handler mHandler = new Handler();

            //Data load is done here
            final Thread refreshTask = new Thread() {
                public void run() {
                    try {
                        if (appData == null)
                            appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), SearchResults.this, "");

                        searchFileLister = new FileLister(DropboxClient.getClient(getString(R.string.ACCESS_TOKEN)),
                                SearchResults.this, new ArrayList<Metadata>(), new ArrayList<VideoData>(), searchInput);
                        searchFileLister.execute();

                        try {
                            searchFileLister.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        videoInfoResults = searchFileLister.getVideoDatas();
                        dropboxSearchData = searchFileLister.getLoadData();

                        refreshDialog.show();
                        sleep(100);
                    } catch (InterruptedException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            };

            //Loading UI Elements in this thread
            final Thread setTask = new Thread() {
                public void run() {
                    loadResults();
                    progressDialog.dismiss();
                    refreshing = false;
                }
            };

            //This thread waits for refresh then loads ui elements in handler.
            Thread waitForRefresh = new Thread() {
                public void run() {
                    try {
                        refreshTask.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.post(setTask);
                }
            };
            refreshTask.start();
            waitForRefresh.start();
        }
    }

    public void loadInBackground() {
        //Data load is done here
        final Thread loadTask = new Thread() {
            public void run() {
                try {
                    if (appData == null)
                        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), SearchResults.this, "");

                    searchFileLister = new FileLister(DropboxClient.getClient(getString(R.string.ACCESS_TOKEN)),
                            SearchResults.this, dropboxSearchData, videoInfoResults, searchInput);
                    searchFileLister.execute();

                    try {
                        searchFileLister.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    videoInfoResults = searchFileLister.getVideoDatas();
                    dropboxSearchData = searchFileLister.getLoadData();
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //Loading UI Elements in this thread
        final Thread setTask = new Thread() {
            public void run() {
                loadResults();
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

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loadMoreBtn:
                loadMore.setVisibility(View.GONE);
                loadInBackground();
                break;
        }
    }
}
