package nz.co.hawkefilms.womenofinfluence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by carl on 26/09/2016.
 * Description:
 * This activity runs every time the app is started from closed.
 */

public class SplashScreen extends Activity {

    private GlobalAppData appData;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), SplashScreen.this, "");
                    sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //checks for notification then starts activity
                    checkNotification();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //checks the notification for any readable data.
    public void checkNotification() {
        VideoData video = null;
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                for ( VideoData videoData : appData.getVideoData()) {
                    if (value != null)
                        if (key.toLowerCase().equals("VIDEO".toLowerCase()) &&
                                value.toString().toLowerCase()
                                        .equals(videoData.getName().replaceFirst("[.][^.]+$", "")
                                                .toLowerCase())) {
                            video = videoData;
                        }
                }
            }
        }
        // [END handle_data_extras]

        //if video data was defined in the data payload
        if (video != null) {
            //Proceed to View_Video
            Intent intent = new Intent(this, ViewVideo.class);
            intent.putExtra("videoIndex", video);
            startActivity(intent);
        }
        else
        {
            //startup like normal
            Intent intent = new Intent(SplashScreen.this, Home.class);
            startActivity(intent);
        }
    }
}