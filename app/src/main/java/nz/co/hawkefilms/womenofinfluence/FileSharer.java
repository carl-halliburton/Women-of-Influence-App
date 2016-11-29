package nz.co.hawkefilms.womenofinfluence;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.ResolvedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

/**
 * Created by nick- on 8/11/2016.
 */

public class FileSharer extends AsyncTask {
    private DbxClientV2 dbxClient;
    private VideoData video; //target video for sharing

    FileSharer(DbxClientV2 dbxClient, VideoData dbVideo){
        this.dbxClient = dbxClient;
        video = dbVideo;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            //get public sharing link for each video on the list or create a new one for each video.
            boolean publicLink = false; // true if a public link exists

            //first get all shared links in the apps dropbox
            for (SharedLinkMetadata link : dbxClient.sharing().listSharedLinks().getLinks()) {
                //check if the link is for the right video file & access is public
                if (link.getPathLower().equals(video.getDbUri())
                        && link.getLinkPermissions().getResolvedVisibility()
                        == ResolvedVisibility.PUBLIC) {
                    publicLink = true;
                    video.setPreviewUrl(link.getUrl());
                }
            }

            //if there is no public link then
            if (!publicLink) { //create a new public viewing link for this video
                video.setPreviewUrl(dbxClient.sharing().createSharedLinkWithSettings(video.getDbUri()).getUrl());
            }
            Log.d("Share Link Created", "Success");
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    public VideoData getVideoData(){
        return video;
    }
}