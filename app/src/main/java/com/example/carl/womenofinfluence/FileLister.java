package com.example.carl.womenofinfluence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.FilePermission;
import com.dropbox.core.v2.sharing.ResolvedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholas.rowley on 9/12/2016.
 */
public class FileLister extends AsyncTask {

    private DbxClientV2 dbxClient;
    private List<VideoData> videoInfoList;
    private List<Metadata> folderContents;
    private List<Metadata> thumbnailFolderContents;
    private Context context;

    FileLister(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;
        this.context = context;
        folderContents = new ArrayList<>();
        videoInfoList = new ArrayList<>();
        thumbnailFolderContents = new ArrayList<>();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {

            //contains metadata for all contents in the folder such as the URI links to each file.
            folderContents = dbxClient.files().listFolder("/videos/").getEntries();

            //create temporary links for each file in the folder
            for (Metadata fileItem : folderContents) {
                //to store temporary urls into a list
                videoInfoList.add(new VideoData(fileItem.getName(), dbxClient.files().getTemporaryLink(fileItem.getPathLower()).getLink(), fileItem.getPathLower()));
            }

            //get public sharing link for each video on the list or create a new one for each video.
            for (int i = 0; i < videoInfoList.size(); i++) {
                boolean publicLink = false; // true if a public link exists

                //first get all shared links in the apps dropbox
                for (SharedLinkMetadata link : dbxClient.sharing().listSharedLinks().getLinks()) {
                    //check if the link is for the right video file & access is public
                    if (link.getPathLower().equals(videoInfoList.get(i).getDbUri())
                            && link.getLinkPermissions().getResolvedVisibility()
                            == ResolvedVisibility.PUBLIC) {
                        publicLink = true;
                        videoInfoList.get(i).setPreviewUrl(link.getUrl());
                    }
                }

                //if there is no public link then
                if (!publicLink){ //create a new public viewing link for this video
                    videoInfoList.get(i).setPreviewUrl(dbxClient.sharing().createSharedLinkWithSettings(videoInfoList.get(i).getDbUri()).getUrl());
                }
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

