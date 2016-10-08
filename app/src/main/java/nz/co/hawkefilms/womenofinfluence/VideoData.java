package nz.co.hawkefilms.womenofinfluence;

/*
Description: THis class stores the video data for a single video.
The object is stored in an arraylist of type VideoData in the GlobalAppData Class
Fields
name - video file name, this will be used as the video title
tempURL - this is the url that is created for each video and used in the viewVideo activity
in the videoView widget
 */

import java.io.Serializable;

/**
 * This class is for storing information about the video that was collected from video storage
 * (Dropbox).
 * Created by nicholas.rowley on 9/12/2016.
 */
public class VideoData implements Serializable{
    private String name;
    private String tempUrl;
    private static final long serialVersionUID = 1L; //required for Serializable

    public VideoData(String vidName, String temporaryUrl)
    {
        name = vidName.replaceFirst("[.][^.]+$", ""); //remove the file extension
        tempUrl = temporaryUrl;
    }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }
}
