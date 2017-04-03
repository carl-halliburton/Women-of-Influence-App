package nz.co.hawkefilms.womenofinfluence;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;

/**
 * Created by carl on 31/10/2016.
 * Manages share function in videoView
 */

class ShareVideo
{
    private Context curContext;

    ShareVideo(Context context) {
        curContext = context;
    }

    /*
    Description:
        Copy's video share link to clipboard
    Parameters: String containing share link
    Return Type: void
    */
    void copyLink(String link) {
        ClipboardManager clipboard = (ClipboardManager) curContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Share Link",link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(curContext, "Link Copied!", Toast.LENGTH_SHORT).show();
    }

    /*
    Description:
        Creates email share intent
    Parameters: String containing share link, String containing video title
    Return Type: void
    */
    void sendEmailIntent(String link, String videoTitle) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ascend - " + videoTitle);
        intent.putExtra(Intent.EXTRA_TEXT, "You might be interested in this video " + "- " + link);
        intent.setData(Uri.parse("mailto:"));
        curContext.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    /*
    Description:
        Creates share dialog for Facebook sharing
    Parameters: String containing share link, Sharedialog, String containing video title
    Return Type: void
    */
    void shareWithFacebook(String link, ShareDialog facebookShareDialog, String videoTitle) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Ascend - Women of Influence with Opinions")
                    .setContentDescription(videoTitle)
                    .setContentUrl(Uri.parse(link))
                    .build();

            facebookShareDialog.show(linkContent);  // Show facebook ShareDialog
        }
    }

    /*
    Description:
        Creates share dialog for Google+
    Parameters: String containing share link, String containing video title
    Return Type: void
    */
    void shareGooglePlus(String link, String videoTitle) {
    }

    /*
    Description:

    Parameters: String containing share link, String containing video title
    Return Type: void
    */
    void shareWithTwitter(String link, String videoTitle) {
        String tweetUrl = "https://twitter.com/intent/tweet?text=&url="
                + "Ascend video - " + videoTitle + ": " + link;
        Uri uri = Uri.parse(tweetUrl);
        curContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /*
    Description:
        Creates Intent for Hangouts
    Parameters: String containing share link, String containing video title
    Return Type: void
    */
    void shareWithHangouts(String link, String videoTitle) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Ascend Video; " + videoTitle + " " + link);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.google.android.talk");
        try {
            curContext.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(curContext, "Hangouts not available", Toast.LENGTH_LONG).show();
        }
    }

    /*
    Description:
        Creates Intent for Whatsapp
    Parameters: String containing share link, String containing video title
    Return Type: void
    */
    void shareWithWhatsApp(String link, String videoTitle) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Ascend Video: " + videoTitle + " " + link);
        try {
            curContext.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(curContext, "WhatsApp not available", Toast.LENGTH_LONG).show();
        }
    }
}