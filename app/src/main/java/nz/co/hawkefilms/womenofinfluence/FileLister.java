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
import java.util.List;

/**
 * Description:
 * This class manages the loading of video data and creation of temporary links for the videos
 * stored in Dropbox
 * See Developer Support Manual for details on video storage management
 */

public class FileLister extends AsyncTask {

    private DbxClientV2 dbxClient;
    private List<VideoData> videoInfoList;
    private List<Metadata> folderContents;
    private Context context;
    private int videosLoaded;
    private int remainingLoads;
    private boolean dbSuccess;

    FileLister(DbxClientV2 dbxClient, Context context, List<Metadata> dropboxLoadData, List<VideoData> loadedVideos) {
        this.dbxClient = dbxClient;
        this.context = context;
        folderContents = dropboxLoadData;
        videoInfoList = loadedVideos;
        videosLoaded = videoInfoList.size();
        remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / 5.0);
        dbSuccess = false;
    }

    /*Async method that connects to Dropbox and gets all videos stored in the videos folder in
    Dropbox*/
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            if (videosLoaded == 0) {
                //contains metadata for all contents in the folder such as the URI links to each file.
                folderContents = dbxClient.files().listFolder("/videos/").getEntries();
            }

            //create temporary links for the next 5 files in the folder
            for (int i = 0; i < 5; i++) {
                //to store temporary urls into a list
                if (videosLoaded < folderContents.size()) {
                    videoInfoList.add(new VideoData(folderContents.get(videosLoaded).getName(), dbxClient.files()
                            .getTemporaryLink(folderContents.get(videosLoaded).getPathLower()).getLink(),
                            folderContents.get(videosLoaded).getPathLower()));
                    videosLoaded++;
                    remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / 5.0);
                    dbSuccess = true;
                }
            }

            remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / 5.0);

            Log.d("Create Links", "Success");
        } catch (DbxException e) {
            e.printStackTrace();
            remainingLoads = (int) Math.ceil((folderContents.size() - videosLoaded) / 5.0);
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