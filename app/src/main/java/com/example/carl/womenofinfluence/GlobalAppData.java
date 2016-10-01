package com.example.carl.womenofinfluence;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class is for storing information and methods relevant to more than one activity on the app.
 * Created by carl on 25/08/2016.
 */
// File Name: GlobalAppData.java
public class GlobalAppData {

    private static GlobalAppData instance = null;
    private Notification notify;
    private Boolean notificationStatus = false;
    private FileLister fileLister;
    private List<VideoData> videoInfoList;

    private GlobalAppData( String ACCESS_TOKEN, Context context ) {
        notify = new Notification();

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
            fileLister = new FileLister(DropboxClient.getClient(ACCESS_TOKEN),
                    context);
            fileLister.execute();
            try {
                fileLister.get(10000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            videoInfoList = new ArrayList<>();
            videoInfoList = fileLister.getVideoDatas();
        }
    }

    public static GlobalAppData getInstance( String ACCESS_TOKEN, Context context ) {
        if(instance == null) {
            instance = new GlobalAppData(ACCESS_TOKEN, context);
        }
        instance.setContext(context);
        return instance;
    }

    public Notification getNotify() {
        return notify;
    }


    public void setContext(Context context) {
        fileLister.setContext(context);
    }

    public boolean getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(Boolean status) {
        notificationStatus = status;
    }

    public List<VideoData> getVideoData(){
        return videoInfoList;
    }

    private void refreshVideoList(){
        videoInfoList = fileLister.getVideoDatas();
    }

    /*This method connects to the dropbox servers to get video data. This method should be run in
    * a separate thread.*/
    public void refreshDropboxFiles( String ACCESS_TOKEN, Context context ){
        fileLister = new FileLister(DropboxClient.getClient(ACCESS_TOKEN),
                context);
        fileLister.execute();

        try {
            fileLister.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        refreshVideoList();
    }


}