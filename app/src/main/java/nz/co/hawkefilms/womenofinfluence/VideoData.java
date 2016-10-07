package nz.co.hawkefilms.womenofinfluence;

import java.io.Serializable;

/**
 * This class is for storing information about the video that was collected from video storage
 * (Dropbox).
 * Created by nicholas.rowley on 9/12/2016.
 */
public class VideoData implements Serializable{
    private String name;
    private String tempUrl;
    private static final long serialVersionUID = 1L;

    public VideoData(String vidName, String temporaryUrl)
    {
        name = vidName.replaceFirst("[.][^.]+$", "");
        tempUrl = temporaryUrl;
    }

    public String getName() {
        return name;
    }

    public String getTempUrl() {
        return tempUrl;
    }
}
