package com.example.carl.womenofinfluence;

import org.json.JSONObject;

/**
 * Created by carl on 29/09/2016.
 */

public class SendViewingStats {

    private JSONObject statsObject;

    public SendViewingStats() {
        JSONObject statsObject = new JSONObject();
    }

    public JSONObject getStatsObject() {
        return statsObject;
    }

    public void EncodeJson(String videoName, Integer viewNum) {
        try{
            statsObject.put("video", videoName);
            statsObject.put("num", viewNum);
        } catch(org.json.JSONException e){
            e.printStackTrace();
        }
        UploadJsonFile();
    }

    public void UploadJsonFile() {

    }
}
