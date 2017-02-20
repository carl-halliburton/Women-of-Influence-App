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

import com.facebook.CallbackManager;
import com.facebook.FacebookDialog;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import static android.R.attr.description;

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

    public void sendEmailIntent(String link, String videoTitle) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ascend - " + videoTitle);
        intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- " + link);
        intent.setData(Uri.parse("mailto:"));
        curContext.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void shareWithFacebook(String link, ShareDialog facebookShareDialog, String videoTitle) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Ascend - Women of Influence with Opinions")
                    .setContentDescription(videoTitle)
                    .setContentUrl(Uri.parse(link))
                    .build();

            facebookShareDialog.show(linkContent);  // Show facebook ShareDialog
        }
    }

    public void shareFacebookMessanger(String link, String videoTitle) {

    }

    public void shareGooglePlus(String link, String videoTitle) {

    }

    public void shareWithTwitter(String link, String videoTitle) {

    }
}