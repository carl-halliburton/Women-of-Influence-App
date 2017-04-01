package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Description:
 * This is a Singleton Class for storing information and methods relevant to more than one activity on the app.
 */

public class GlobalAppData {

    private static GlobalAppData instance = null;
    private FileLister fileLister;
    private List<VideoData> videoInfoList;
    private List<Metadata> dropboxLoadData; //data for loading remaining dropbox videos

    private GlobalAppData( String ACCESS_TOKEN, Context context, String searchString ) {

        //checks if access token is not set.
        if (ACCESS_TOKEN.equals("ACCESS_TOKEN")) {
            new AlertDialog.Builder(context)
                    .setTitle("WARNING: ACCESS TOKEN NOT SET")
                    .setMessage("Invalid access token detected. Without a valid token this " +
                            "application will not run properly. If you are a user please reinstall " +
                            "the app. If you are still experiencing this issue please contact " +
                            "support.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            //execute filelister and get Dropbox videos
            fileLister = new FileLister(DropboxClient.getClient(ACCESS_TOKEN),
                    context, new ArrayList<Metadata>(), new ArrayList<VideoData>(), searchString);
            fileLister.execute();
            try {
                fileLister.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            videoInfoList = new ArrayList<>();
            videoInfoList = fileLister.getVideoDatas();

            dropboxLoadData = new ArrayList<>();
            dropboxLoadData = fileLister.getLoadData();
        }
    }

    public static GlobalAppData getInstance( String ACCESS_TOKEN, Context context, String searchString ) {
        if(instance == null) {
            instance = new GlobalAppData(ACCESS_TOKEN, context, searchString);
        }
        instance.setContext(context);
        return instance;
    }

    public void setContext(Context context) {
        fileLister.setContext(context);
    }

    public List<VideoData> getVideoData(){
        return videoInfoList;
    }

    private void refreshVideoList(){
        videoInfoList = fileLister.getVideoDatas();
        dropboxLoadData = fileLister.getLoadData();
    }

    /*This method connects to the dropbox servers to get video data. This method should be run in
    * a separate thread. Note: This method loads the videos from scratch*/
    public void refreshDropboxFiles( String ACCESS_TOKEN, Context context, String searchString ){
        fileLister = new FileLister(DropboxClient.getClient(ACCESS_TOKEN),
                context, new ArrayList<Metadata>(), new ArrayList<VideoData>(), searchString);
        fileLister.execute();

        try {
            fileLister.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        refreshVideoList();
    }

    /*url is saved for reuse.*/
    public void updateVideoUrl (VideoData video){
        for (VideoData videoData : videoInfoList){
            if (video.getDbUri().equals(videoData.getDbUri())){
                videoData.setPreviewUrl(video.getSharingUrl());
            }
        }
    }

    /*This method is for loading dropbox files in the background until fully loaded*/
    public void loadDropboxFiles( String ACCESS_TOKEN, Context context, String searchString ){
        fileLister = new FileLister(DropboxClient.getClient(ACCESS_TOKEN),
                context, dropboxLoadData, videoInfoList, searchString);
        fileLister.execute();

        try {
            fileLister.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        refreshVideoList();
    }

    public int loadsRemaining(){
        return fileLister.remainingLoads();
    }

    public boolean dbSuccess() { return fileLister.dbConnectionSuccessfull(); }
}