package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
    private final int LOAD_TIME = 3000;

    private GlobalAppData( String ACCESS_TOKEN, Context context ) {

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
                fileLister.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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

    public void setContext(Context context) {
        fileLister.setContext(context);
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
            fileLister.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        refreshVideoList();
    }
}