package com.example.carl.womenofinfluence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholas.rowley on 9/12/2016.
 */
public class FileLister extends AsyncTask {

    private DbxClientV2 dbxClient;
    private List<VideoData> videoDatas;
    private List<Metadata> folderContents;
    private Context context;

    FileLister(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;
        this.context = context;
        folderContents = new ArrayList<Metadata>();
        videoDatas = new ArrayList<VideoData>();
    }

    @Override
    protected Object doInBackground(Object[] params) {
            try {

                //contains metadata for all contents in the folder such as the URI links to each file.
                folderContents = dbxClient.files().listFolder("/videos/").getEntries();

                //create temporary links for each file in the folder
                for (Metadata fileItem : folderContents) {
                    //to store temporary urls into a list
                    videoDatas.add(new VideoData(fileItem.getName(), dbxClient.files().getTemporaryLink(fileItem.getPathLower()).getLink()));
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
        Toast.makeText(context, "Server connection successful", Toast.LENGTH_SHORT).show();
    }

    public List<VideoData> getVideoDatas() {
        return videoDatas;
    }
}

