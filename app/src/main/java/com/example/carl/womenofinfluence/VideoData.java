package com.example.carl.womenofinfluence;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by nicholas.rowley on 9/12/2016.
 */
public class VideoData implements Serializable{
    private String name;
    private String tempUrl;
    private String uri;
    private Bitmap thumbnail;
    private static final long serialVersionUID = 1L;

    VideoData(String vidName, String temporaryUrl)
    {
        name = vidName;
        tempUrl = temporaryUrl;
        //uri = newUri;
        //thumbnail = newThumbnail;
    }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public String getUri() {
        return uri;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
