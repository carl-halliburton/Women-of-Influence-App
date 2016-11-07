package nz.co.hawkefilms.womenofinfluence;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by carl on 31/10/2016.
 */

public class ShareVideo
{

    public ShareVideo() {

    }

    public void sendEmailIntent() {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject - Name of Video");
            intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- PLACE LINK HERE");
            intent.setData(Uri.parse("mailto:"));
            //startActivity(Intent.createChooser(intent, "Send Email"));
        }

    public void sendSmsIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "default content");
        sendIntent.setType("vnd.android-dir/mms-sms");
        //startActivity(sendIntent);
    }
}
