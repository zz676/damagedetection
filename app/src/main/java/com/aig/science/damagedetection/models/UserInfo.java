package com.aig.science.damagedetection.models;


import com.aig.science.damagedetection.helper.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {

    private String firstName, lastName, phoneNo, emailId, address, username, passcode, userId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return passcode;
    }

    public void setPassword(String passcode) {
        this.passcode = passcode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JSONObject toInsertJSON(UserInfo userInfo) {

        try {
            JSONObject object = new JSONObject();
            object.put("transaction_type", "MODIFICATION_PUSH");
            object.put("action", "INSERT");
            object.put("tablename", DatabaseHelper.TABLE_USER_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_USERNAME)
                    .put("value", ""));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_PASSWORD)
                    .put("value", ""));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_FIRST_NAME)
                    .put("value", userInfo.getFirstName()));
            array.put(4, new JSONObject().put("column", DatabaseHelper.KEY_LAST_NAME)
                    .put("value", userInfo.getLastName()));
            array.put(5, new JSONObject().put("column", DatabaseHelper.KEY_PHONE_NO)
                    .put("value", userInfo.getPhoneNo()));
            array.put(6, new JSONObject().put("column", DatabaseHelper.KEY_EMAIL_ID)
                    .put("value", userInfo.getEmailId()));
            array.put(7, new JSONObject().put("column", DatabaseHelper.KEY_ADDRESS)
                    .put("value", userInfo.getAddress()));

            object.put("data", array);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject toUpdateJSONLogin(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PUSH");

            object.put("action", "UPDATE");
            object.put("tablename", DatabaseHelper.TABLE_USER_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USERNAME)
                    .put("value", userInfo.getUsername()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_PASSWORD)
                    .put("value", userInfo.getPassword()));

            JSONArray array2 = new JSONArray();
            array2.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));

            object.put("data", array);
            object.put("where", array2);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject toUpdateJSON(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PUSH");

            object.put("action", "UPDATE");
            object.put("tablename", DatabaseHelper.TABLE_USER_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_FIRST_NAME)
                    .put("value", userInfo.getFirstName()));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_LAST_NAME)
                    .put("value", userInfo.getLastName()));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_PHONE_NO)
                    .put("value", userInfo.getPhoneNo()));
            array.put(4, new JSONObject().put("column", DatabaseHelper.KEY_EMAIL_ID)
                    .put("value", userInfo.getEmailId()));
            array.put(5, new JSONObject().put("column", DatabaseHelper.KEY_ADDRESS)
                    .put("value", userInfo.getAddress()));

            JSONArray array2 = new JSONArray();
            array2.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));

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
     *
     * @param userInfo
     * @return
     */
    public JSONObject toQueryJSON(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PULL");

            object.put("action", "QUERY");
            object.put("tablename", DatabaseHelper.TABLE_USER_INFO);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USERNAME)
                    .put("value", userInfo.getUsername()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_PASSWORD)
                    .put("value", userInfo.getPassword()));

            object.put("data", array);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
