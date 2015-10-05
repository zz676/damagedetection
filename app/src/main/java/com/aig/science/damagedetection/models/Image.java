package com.aig.science.damagedetection.models;

import android.content.ContentValues;
import android.net.Uri;

import com.aig.science.damagedetection.helper.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Image {

    private String imageURI;
    private String claimId;
    private String imageId;
    private String imageType;

    public Image() {
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public JSONObject toInsertJSON(Image image){

        try{
            JSONObject object = new JSONObject();
            object.put("transaction_type", "MODIFICATION_PUSH");
            object.put("action", "INSERT");
            object.put("tablename", DatabaseHelper.TABLE_IMAGE_DETAILS);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_CLAIM_ID)
                    .put("value", image.getClaimId()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_IMAGE_ID)
                    .put("value",image.getImageId()));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_IMAGE_PATH)
                    .put("value", image.getImageURI()));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_IMAGE_TYPE)
                    .put("value",image.getImageType()));

            object.put("data", array);
            return object;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * query info by username
     *
     * @param claimList
     * @return
     */
    public static JSONObject toQueryJSON(List<Claim> claimList) {
        Map paramMap = new HashMap();
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PULL");
            object.put("action", "QUERY");
            object.put("tablename", DatabaseHelper.TABLE_IMAGE_DETAILS);

            for(Claim claim : claimList){
                JSONObject tempObject = new JSONObject();
                tempObject.put(DatabaseHelper.KEY_CLAIM_ID, claim.getClaimId());
                jsonArray.put(tempObject.toString());
                System.out.println(tempObject.toString());
            }
            object.put("data",jsonArray);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toQueryJSON(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PULL");
            object.put("action", "QUERY");
            object.put("tablename", DatabaseHelper.TABLE_POLICY_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));

            object.put("data", array);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
