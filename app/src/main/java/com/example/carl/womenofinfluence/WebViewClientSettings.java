package com.example.carl.womenofinfluence;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nick- on 29/08/2016.
 * Currently handles how url links in the WebView are handled.
 */
public class WebViewClientSettings extends WebViewClient {
    //Opens non video URL links in the browser instead.
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        if (url != null && !url.startsWith("https://player.vimeo.com/video/") && url.startsWith("https://")) {
            view.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        } else {
            return false;
        }
    }
}
