package nz.co.hawkefilms.womenofinfluence;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

class ShareVideo {

    private Context thisContext;

    ShareVideo(Context context) {
        thisContext = context;
    }

    void sendEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject - Name of Video");
        intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- PLACE LINK HERE");
        intent.setData(Uri.parse("mailto:"));
        thisContext.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    void sendSmsIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "You might be interested in this video " + "- PLACE LINK HERE");
        sendIntent.setType("vnd.android-dir/mms-sms");
        thisContext.startActivity(sendIntent);
    }
}
