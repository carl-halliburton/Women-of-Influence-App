package nz.co.hawkefilms.womenofinfluence;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 31/10/2016.
 */

public class ShareVideo
{
    private Context curContext;

    public ShareVideo(Context context) {
        curContext = context;
    }

    public void copyLink(String link) {
        ClipboardManager clipboard = (ClipboardManager) curContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Share Link",link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(curContext, "Link Copied!", Toast.LENGTH_SHORT).show();
    }

    public void sendEmailIntent(String link) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject - Name of Video");
            intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- " + link);
            intent.setData(Uri.parse("mailto:"));
            curContext.startActivity(Intent.createChooser(intent, "Send Email"));
        }

    public void shareWithFacebook(String link) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);;
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ascend Video - " + link);
        curContext.startActivity(Intent.createChooser(shareIntent, "Share your thoughts"));
    }

    public void shareWithTwitter(String link) {
    }
}
