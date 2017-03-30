package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.ResolvedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * This class manages the loading of video data and creation of temporary links for the videos
 * stored in Dropbox
 * See Developer Support Manual for details on video storage management
 */

public class FileLister extends AsyncTask<Object, Void, Object>{

    private DbxClientV2 dbxClient;
    private List<VideoData> videoInfoList;
    private List<Metadata> folderContents;
    private Context context;
    private int videosLoaded;
    private int remainingLoads;
    private boolean dbSuccess;
    private String searchString;
    private boolean searchEnabled;
    private static final int LOADAMOUNT = 5; //number of videos loaded with a single execution of the class. Less is faster.

    FileLister(DbxClientV2 dbxClient, Context context, List<Metadata> dropboxLoadData, List<VideoData> loadedVideos, String searchInput) {
        this.dbxClient = dbxClient;
        this.context = context;
        folderContents = dropboxLoadData;
        videoInfoList = loadedVideos;

        videosLoaded = videoInfoList.size();
        remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / (float) LOADAMOUNT);
        searchString = searchInput;

        searchEnabled = !searchString.equals("");
        dbSuccess = false;
    }

    /*Async method that connects to Dropbox and gets all videos stored in the videos folder in
    Dropbox*/
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            if(!searchEnabled) {
                if (videosLoaded == 0) {
                    //contains metadata for all contents in the folder such as the URI links to each file.
                    folderContents = dbxClient.files().listFolder("/videos/").getEntries();
                }
            } else {
                if (videosLoaded == 0) {
                    //contains metadata for all contents in the folder such as the URI links to each file.
                    folderContents = dbxClient.files().listFolder("/videos/").getEntries();

                    //go through and delete all irrelevant results
                    List<Metadata> resultsList = new ArrayList<>();
                    CharSequence searchSequence = searchString.toLowerCase();
                    for(Metadata video : folderContents){
                        if (video.getName().toLowerCase().contains(searchSequence)){
                            resultsList.add(video);
                        }
                    }
                    //reverse metadata list so that latest video is always the feature and
                    // the video gallery is ordered from recent to least recent
                    folderContents = resultsList;
                }
            }

            //create temporary links for the next few files in the folder
            for (int i = 0; i < LOADAMOUNT; i++) {
                //to store temporary urls into a list
                if (videosLoaded < folderContents.size()) {
                    int entryToLoad = folderContents.size() - 1 - videosLoaded; //load in reverse
                    videoInfoList.add(new VideoData(folderContents.get(entryToLoad).getName(), dbxClient.files()
                            .getTemporaryLink(folderContents.get(entryToLoad).getPathLower()).getLink(),
                            folderContents.get(entryToLoad).getPathLower()));
                    videosLoaded++;
                    remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / (float) LOADAMOUNT);
                }
            }
            dbSuccess = true;

            remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / (float) LOADAMOUNT);

            Log.d("Create Links", "Success");
        } catch (DbxException e) {
            e.printStackTrace();
            remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / (float) LOADAMOUNT);
            dbSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public List<VideoData> getVideoDatas() {
        return videoInfoList;
    }

    public List<Metadata> getLoadData() { return folderContents; }

    public int remainingLoads() { return remainingLoads; }

    public boolean dbConnectionSuccessfull(){
        return dbSuccess;
    }
}