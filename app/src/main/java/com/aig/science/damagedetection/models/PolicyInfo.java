package com.aig.science.damagedetection.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.utilities.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PolicyInfo implements Parcelable, Comparable<PolicyInfo> {

    private String policyNo;
    private String make, model, color, licenseNo, vin, type;
    private String policyId, userId;

    public PolicyInfo(){}

    public PolicyInfo(Parcel source){
        readFromParcel(source);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    @Override
    public int compareTo(PolicyInfo policy) {
        return this.getPolicyNo().compareTo(policy.getPolicyNo());
    }

    public JSONObject toInsertJSON(PolicyInfo policyInfo){

        try{
            JSONObject object = new JSONObject();
            object.put("transaction_type", "MODIFICATION_PUSH");
            object.put("action", "INSERT");
            object.put("tablename", DatabaseHelper.TABLE_POLICY_INFO);

            object.put(DatabaseHelper.KEY_USER_ID, policyInfo.getUserId());
            object.put(DatabaseHelper.KEY_POLICY_ID, policyInfo.getPolicyId());

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_ID)
                    .put("value",policyInfo.getPolicyId()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", policyInfo.getUserId()));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_NO)
                    .put("value", policyInfo.getPolicyNo()));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_MAKE)
                    .put("value",policyInfo.getMake()));
            array.put(4, new JSONObject().put("column", DatabaseHelper.KEY_MODEL)
                    .put("value",policyInfo.getModel()));
            array.put(5, new JSONObject().put("column", DatabaseHelper.KEY_COLOR)
                    .put("value", policyInfo.getColor()));
            array.put(6, new JSONObject().put("column", DatabaseHelper.KEY_LICENSE_NO)
                    .put("value",policyInfo.getLicenseNo()));
            array.put(7, new JSONObject().put("column", DatabaseHelper.KEY_VIN)
                    .put("value",policyInfo.getVin()));
            object.put("data", array);
            return object;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject toUpdateJSON(PolicyInfo policyInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PUSH");

            object.put("action", "UPDATE");
            object.put("tablename", DatabaseHelper.TABLE_POLICY_INFO);

            object.put(DatabaseHelper.KEY_USER_ID, policyInfo.getUserId());
            object.put(DatabaseHelper.KEY_POLICY_ID, policyInfo.getPolicyId());

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_NO)
                    .put("value", policyInfo.getPolicyNo()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_MAKE)
                    .put("value",policyInfo.getMake()));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_MODEL)
                    .put("value",policyInfo.getModel()));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_COLOR)
                    .put("value", policyInfo.getColor()));
            array.put(4, new JSONObject().put("column", DatabaseHelper.KEY_LICENSE_NO)
                    .put("value",policyInfo.getLicenseNo()));
            array.put(5, new JSONObject().put("column", DatabaseHelper.KEY_VIN)
                    .put("value",policyInfo.getVin()));

            JSONArray array2 = new JSONArray();
            array2.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", policyInfo.getUserId()));
            array2.put(1, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_ID)
                    .put("value",policyInfo.getPolicyId()));

            object.put("data", array);
            object.put("where", array2);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * query info by username
     * @param userInfo
     * @return
     */
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

    /**
     * generate the json for deleting a policy
     * @param policyInfo
     * @return
     */
    public static JSONObject toDeleteJSON(PolicyInfo policyInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PUSH");
            object.put("action", "DELETE");
            object.put("tablename", DatabaseHelper.TABLE_POLICY_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_ID)
                    .put("value", policyInfo.getPolicyId()));

            object.put("data", array);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel source){

        policyNo = source.readString();
        make = source.readString();
        model = source.readString();
        color = source.readString();
        licenseNo = source.readString();
        vin = source.readString();
        type = source.readString();
        policyId = source.readString();
        userId = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(policyNo);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(color);
        dest.writeString(licenseNo);
        dest.writeString(vin);
        dest.writeString(type);
        dest.writeString(policyId);
        dest.writeString(userId);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public PolicyInfo createFromParcel(Parcel source) {
            return new PolicyInfo(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };
}
