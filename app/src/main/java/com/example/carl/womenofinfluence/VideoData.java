package com.example.carl.womenofinfluence;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * This class is for storing information about the video that was collected from video storage
 * (Dropbox).
 * Created by nicholas.rowley on 9/12/2016.
 */
public class VideoData implements Serializable{
    private String name;
    private String tempUrl;
    private String dropboxUri;
    private String sharingUrl; //public sharing preview url
    private static final long serialVersionUID = 1L;

    public VideoData(String vidName, String temporaryUrl, String dbUri)
    {
        name = vidName.replaceFirst("[.][^.]+$", "");
        tempUrl = temporaryUrl;
        dropboxUri = dbUri;
        sharingUrl = "Error: cannot find url";
    }

    public void setPreviewUrl(String previewUrl) {
        if (previewUrl != null) {
            sharingUrl = previewUrl;
        }
    }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public String getDbUri() { return dropboxUri; }

    public String getSharingUrl() { return sharingUrl; }
}
