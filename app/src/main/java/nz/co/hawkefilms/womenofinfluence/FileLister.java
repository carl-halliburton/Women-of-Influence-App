package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
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

    FileLister(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;
        this.context = context;
        folderContents = new ArrayList<>();
        videoInfoList = new ArrayList<>();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {

            //contains metadata for all contents in the folder such as the URI links to each file.
            folderContents = dbxClient.files().listFolder("/videos/").getEntries();

            //create temporary links for each file in the folder
            for (Metadata fileItem : folderContents) {
                //to store temporary urls into a list
                videoInfoList.add(new VideoData(fileItem.getName(), dbxClient.files().getTemporaryLink(fileItem.getPathLower()).getLink()));
            }
            Log.d("Create Links", "Success");
        } catch (DbxException e) {
            e.printStackTrace();
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
}