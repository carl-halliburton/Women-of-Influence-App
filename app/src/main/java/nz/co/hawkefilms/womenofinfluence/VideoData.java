package nz.co.hawkefilms.womenofinfluence;

import java.io.Serializable;

/**
 * Description: THis class stores the video data for a single video.
 * The object is stored in an arraylist of type VideoData in the GlobalAppData Class
 * Fields
 * name - video file name, this will be used as the video title
 * tempURL - this is the url that is created for each video and used in the viewVideo activity
 * in the videoView widget
 */

public class VideoData implements Serializable{
    private String name;
    private String tempUrl;
    private String dropboxUri;
    private String sharingUrl; //public sharing preview url
    private String videoStatsName;
    public static final String SHARINGFAILMESSAGE = "Error: cannot find url";
    private static final long serialVersionUID = 1L; //required for Serializable

    public VideoData(String vidName, String temporaryUrl, String dbUri)
    {
        name = vidName.replaceFirst("[.][^.]+$", ""); //remove the file extension
        tempUrl = temporaryUrl;
        dropboxUri = dbUri;
        sharingUrl = SHARINGFAILMESSAGE;
        videoStatsName = "views_" + name.replaceAll(" ", "_").toLowerCase();
    }

    public void setPreviewUrl(String previewUrl) {
        if (previewUrl != null) {
            sharingUrl = previewUrl;
        }
    }

    public String getDbUri() { return dropboxUri; }

    public String getSharingUrl() { return sharingUrl; }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public String getVideoStatsName(){
        return videoStatsName;
    }
}
