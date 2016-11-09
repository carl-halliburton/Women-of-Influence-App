package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by carl on 31/10/2016.
 */

public class ShareVideo
{
    private Context curContext;

    public ShareVideo(Context context) {
        curContext = context;
    }

    public void sendEmailIntent(String link) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject - Name of Video");
            intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- " + link);
            intent.setData(Uri.parse("mailto:"));
            curContext.startActivity(Intent.createChooser(intent, "Send Email"));
        }
}
