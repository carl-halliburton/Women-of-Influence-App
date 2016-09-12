package com.example.carl.womenofinfluence;

import java.io.Serializable;

/**
 * Created by nicholas.rowley on 9/12/2016.
 */
public class VideoData implements Serializable{
    private String name;
    private String tempUrl;

    VideoData(String vidName, String temporaryUrl)
    {
        name = vidName;
        tempUrl = temporaryUrl;
    }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }

}
