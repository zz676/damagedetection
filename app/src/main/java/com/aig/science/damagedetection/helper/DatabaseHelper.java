package com.aig.science.damagedetection.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aig.science.damagedetection.logger.Log;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.models.Image;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.models.PolicyMaster;
import com.aig.science.damagedetection.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Log
    private static final String TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DamageDetection";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Table Names
    public static final String TABLE_USER_INFO = "USER_INFO";
    public static final String TABLE_VEHICLE_INFO = "VEHICLE_INFO";
    public static final String TABLE_POLICY_INFO = "POLICY_INFO";
    public static final String TABLE_POLICY_MASTER = "POLICY_MASTER";
    public static final String TABLE_CLAIMS = "CLAIMS";
    public static final String TABLE_IMAGE_DETAILS = "IMAGE_DETAILS";

    // Common column names
    private static final String KEY_ID = "id";

    // USER INFO Table - column names
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_PHONE_NO = "phone_no";
    public static final String KEY_EMAIL_ID = "email_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_USERNAME = "username";
    //public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASSWORD = "passcode";

    // vehicle info Table - column names
    public static final String KEY_VEHICLE_ID = "vehicle_id";
    public static final String KEY_MAKE = "make";
    public static final String KEY_MODEL = "model";
    public static final String KEY_COLOR = "color";
    public static final String KEY_LICENSE_NO = "license_no";
    public static final String KEY_VIN = "vin";

    // policy info Table - column names
    public static final String KEY_POLICY_ID = "policy_id";
    public static final String KEY_POLICY_NO = "policy_no";

    //CLAIMS TABLE- COLUMN NAMES
    public static final String KEY_CLAIM_ID = "claim_id";
    public static final String KEY_COST = "cost";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_STATUS = "claim_status";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_SUBMITTED_TIME = "submitted_time";

    //image details - column names
    public static final String KEY_IMAGE_ID = "image_id";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_IMAGE_TYPE = "image_type";

    //user_info table create statement
    private static final String CREATE_USER_INFO = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USER_INFO + "("
            + KEY_USER_ID + " TEXT PRIMARY KEY,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_LAST_NAME + " TEXT,"
            + KEY_PHONE_NO + " TEXT,"
            + KEY_EMAIL_ID + " TEXT,"
            + KEY_ADDRESS + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PASSWORD + " TEXT"
            + ")";

    private static final String CREATE_POLICY_INFO = "CREATE TABLE IF NOT EXISTS "
            + TABLE_POLICY_INFO + "("
            + KEY_USER_ID + " TEXT REFERENCES " + TABLE_USER_INFO + "(" + KEY_USER_ID + "),"
            + KEY_POLICY_ID + " TEXT PRIMARY KEY,"
            + KEY_POLICY_NO + " TEXT,"
            + KEY_MAKE + " TEXT,"
            + KEY_MODEL + " TEXT,"
            + KEY_COLOR + " TEXT,"
            + KEY_LICENSE_NO + " TEXT,"
            + KEY_VIN + " TEXT"
            + ")";
    /*

        private static final String CREATE_POLICY_INFO = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_POLICY_INFO + "("
                    + KEY_USER_ID + " TEXT REFERENCES " + TABLE_USER_INFO + "(" + KEY_USER_ID + "),"
                    + KEY_POLICY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_POLICY_NO + " TEXT"
                    + ")";
        */
    private static final String CREATE_POLICY_MASTER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_POLICY_MASTER + "("
            + KEY_POLICY_NO + " TEXT,"
            + KEY_MAKE + " TEXT,"
            + KEY_MODEL + " TEXT,"
            + KEY_COLOR + " TEXT,"
            + KEY_LICENSE_NO + " TEXT,"
            + KEY_VIN + " TEXT,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_LAST_NAME + " TEXT,"
            + KEY_PHONE_NO + " TEXT,"
            + KEY_EMAIL_ID + " TEXT,"
            + KEY_ADDRESS + " TEXT"
            + ")";
    private static final String CREATE_CLAIMS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CLAIMS + "("
            + KEY_USER_ID + " TEXT REFERENCES " + TABLE_USER_INFO + "(" + KEY_USER_ID + "),"
            + KEY_POLICY_ID + " TEXT REFERENCES " + TABLE_POLICY_INFO + "(" + KEY_POLICY_ID + "),"
            + KEY_CLAIM_ID + " TEXT PRIMARY KEY,"
            + KEY_STATUS + " TEXT,"
            + KEY_LONGITUDE + " REAL,"
            + KEY_LATITUDE + " REAL,"
            + KEY_SUBMITTED_TIME + " TEXT"
            + ")";

    private static final String CREATE_IMAGE_DETAILS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_IMAGE_DETAILS + "("
            + KEY_CLAIM_ID + " TEXT REFERENCES " + TABLE_CLAIMS + "(" + KEY_CLAIM_ID + "),"
            + KEY_IMAGE_ID + " TEXT PRIMARY KEY,"
            + KEY_IMAGE_PATH + " TEXT,"
            + KEY_IMAGE_TYPE + " TEXT"
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_INFO);
        db.execSQL(CREATE_POLICY_INFO);
        //db.execSQL(CREATE_VEHICLE_INFO);
        db.execSQL(CREATE_POLICY_MASTER);
        db.execSQL(CREATE_CLAIMS);
        db.execSQL(CREATE_IMAGE_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_USER_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_POLICY_INFO);
        //db.execSQL("DROP TABLE IF EXISTS " + CREATE_VEHICLE_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_POLICY_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CLAIMS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_IMAGE_DETAILS);
        onCreate(db);
    }

    public void createPolicyMaster(PolicyMaster policyMaster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_POLICY_NO, policyMaster.getPolicyNo());
        values.put(KEY_MAKE, policyMaster.getMake());
        values.put(KEY_MODEL, policyMaster.getModel());
        values.put(KEY_COLOR, policyMaster.getColor());
        values.put(KEY_LICENSE_NO, policyMaster.getLicenseNo());
        values.put(KEY_VIN, policyMaster.getVin());
        values.put(KEY_FIRST_NAME, policyMaster.getFirstName());
        values.put(KEY_LAST_NAME, policyMaster.getLastName());
        values.put(KEY_PHONE_NO, policyMaster.getPhoneNo());
        values.put(KEY_ADDRESS, policyMaster.getAddress());
        values.put(KEY_EMAIL_ID, policyMaster.getEmailId());

        long trip_id = db.insert(TABLE_POLICY_MASTER, null, values);
    }

    public boolean isTableExists(String tableName) {
        try {
            SQLiteDatabase mDatabase = getReadableDatabase();
            Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
        } catch (Exception ex) {
            Log.d(TAG, "Error when executing 'isTableExists': " + ex.getMessage());
        }
        return false;
    }


    public void createUserInfo(UserInfo userInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, userInfo.getUserId());
            values.put(KEY_FIRST_NAME, userInfo.getFirstName());
            values.put(KEY_LAST_NAME, userInfo.getLastName());
            values.put(KEY_PHONE_NO, userInfo.getPhoneNo());
            values.put(KEY_ADDRESS, userInfo.getAddress());
            if (userInfo.getUsername() != null)
                values.put(KEY_USERNAME, userInfo.getUsername());
            values.put(KEY_EMAIL_ID, userInfo.getEmailId());
            db.insert(TABLE_USER_INFO, null, values);
            db.close();
        } catch (Exception ex) {
            Log.d(TAG, "Insert data into " + TABLE_USER_INFO + " failed: " + ex.getMessage());
        }
    }

    public void insertLoginInfo(UserInfo userInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString =
                "SELECT * FROM " + TABLE_USER_INFO + " WHERE user_id = ? ";
        String[] whereArgs = new String[]{
                KEY_USER_ID
        };

        Cursor cursor = db.rawQuery(queryString, whereArgs);
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnIndexOrThrow(KEY_USER_ID);
            } while (cursor.moveToNext());
        }
        String[] whereArg = new String[]{userInfo.getUserId()};

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        ContentValues val = new ContentValues();
        val.put(KEY_USERNAME, userInfo.getUsername());
        val.put(KEY_PASSWORD, userInfo.getPassword());
        db.update(TABLE_USER_INFO, val, "user_id = ?", whereArg);
    }

    public boolean validateUser(String userNameStr, String passwordStr) {
        String selectQuery = "SELECT " + KEY_USERNAME + " , " + KEY_PASSWORD + " FROM " + TABLE_USER_INFO + " where " + KEY_USERNAME + " = '"
                + userNameStr + "' and " + KEY_PASSWORD + " = '" + passwordStr + "'";
        boolean isValidUser = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                isValidUser = true;
            }
        } finally {
            cursor.close();
        }
        return isValidUser;
    }

    public int getNoOfPolicies(String userId) {
        int numberOfPolicies = 0;
        String selectQuery = "SELECT  count(*) FROM " + TABLE_POLICY_INFO + " where " + KEY_USER_ID + " = '"
                + userId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                numberOfPolicies = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return numberOfPolicies;
    }

    public void createPolicyInfo(PolicyInfo policyInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, policyInfo.getUserId());
        values.put(KEY_POLICY_ID, policyInfo.getPolicyId());
        values.put(KEY_POLICY_NO, policyInfo.getPolicyNo());
        values.put(KEY_MAKE, policyInfo.getMake());
        values.put(KEY_MODEL, policyInfo.getModel());
        values.put(KEY_COLOR, policyInfo.getColor());
        values.put(KEY_LICENSE_NO, policyInfo.getLicenseNo());
        values.put(KEY_VIN, policyInfo.getVin());
        db.insert(TABLE_POLICY_INFO, null, values);
        db.close();
    }

    public ArrayList<PolicyInfo> getPolicyDetails(String userID) {
        String selectQuery = "SELECT  * FROM " + TABLE_POLICY_INFO + " where user_id = '" + userID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
        if (cursor.moveToFirst()) {
            do {
                PolicyInfo policyInfo = new PolicyInfo();
                policyInfo.setUserId(cursor.getString(0));
                policyInfo.setPolicyId(cursor.getString(1));
                policyInfo.setPolicyNo(cursor.getString(2));
                policyInfo.setMake(cursor.getString(3));
                policyInfo.setModel(cursor.getString(4));
                policyInfo.setColor(cursor.getString(5));
                policyInfo.setLicenseNo(cursor.getString(6));
                policyInfo.setVin(cursor.getString(7));
                policyList.add(policyInfo);
            } while (cursor.moveToNext());
        }
        return policyList;
    }

    /**
     * get all the policies of one user
     *
     * @param userID
     * @return
     */
    public ArrayList<PolicyInfo> getUserPolicies(String userID) {
        String selectQuery = "SELECT  * FROM " + TABLE_POLICY_INFO + " WHERE " + KEY_USER_ID + " = '" + userID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
        if (cursor.moveToFirst()) {
            do {
                PolicyInfo policyInfo = new PolicyInfo();
                policyInfo.setUserId(cursor.getString(0));
                policyInfo.setPolicyId(cursor.getString(1));
                policyInfo.setPolicyNo(cursor.getString(2));
                policyInfo.setMake(cursor.getString(3));
                policyInfo.setModel(cursor.getString(4));
                policyInfo.setColor(cursor.getString(5));
                policyInfo.setLicenseNo(cursor.getString(6));
                policyInfo.setVin(cursor.getString(7));
                policyList.add(policyInfo);
            } while (cursor.moveToNext());
        }
        return policyList;
    }

    public Boolean deletePolicy(String userID, String policyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_POLICY_INFO, KEY_USER_ID + " = '" + userID + "' and " + KEY_POLICY_ID + " = '" + policyID + "'", null) > 0;
    }

    public void editPolicyInfo(PolicyInfo policyInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString =
                "SELECT * FROM " + TABLE_POLICY_INFO + " WHERE user_id = ? and policy_id = ?";
        String[] whereArgs = new String[]{
                KEY_USER_ID, KEY_POLICY_ID
        };
        Cursor cursor = db.rawQuery(queryString, whereArgs);
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnIndexOrThrow(KEY_USER_ID);
                cursor.getColumnIndexOrThrow(KEY_POLICY_ID);
            } while (cursor.moveToNext());
        }
        String[] whereArg = new String[]{policyInfo.getUserId(), policyInfo.getPolicyId()};

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        ContentValues values = new ContentValues();
        values.put(KEY_POLICY_NO, policyInfo.getPolicyNo());
        values.put(KEY_MAKE, policyInfo.getMake());
        values.put(KEY_MODEL, policyInfo.getModel());
        values.put(KEY_COLOR, policyInfo.getColor());
        values.put(KEY_LICENSE_NO, policyInfo.getLicenseNo());
        values.put(KEY_VIN, policyInfo.getVin());
        db.update(TABLE_POLICY_INFO, values, "user_id = ? and policy_id = ? ", whereArg);
    }

    public UserInfo getuserDetails(String userID) {
        String selectQuery = "SELECT  * FROM " + TABLE_USER_INFO + " where " + KEY_USER_ID + " = '"
                + userID + "'";
        ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        UserInfo userInfo = null;
        if (cursor.moveToFirst()) {
            do {
                userInfo = new UserInfo();
                userInfo.setUserId(cursor.getString(0));
                userInfo.setFirstName(cursor.getString(1));
                userInfo.setLastName(cursor.getString(2));
                userInfo.setPhoneNo(cursor.getString(3));
                userInfo.setEmailId(cursor.getString(4));
                userInfo.setAddress(cursor.getString(5));
            } while (cursor.moveToNext());
        }
        return userInfo;
    }

    public String getUserID(String userName, String password) {
        String userID = "";
        String selectQuery = "SELECT  DISTINCT " + KEY_USER_ID + "  FROM " + TABLE_USER_INFO + " where " + KEY_EMAIL_ID + " = '"
                + userName + "' AND " + KEY_PASSWORD + " ='" + password + "' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                userID = cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        return userID;
    }

    public void editUserInfo(UserInfo userInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString =
                "SELECT * FROM " + TABLE_USER_INFO + " WHERE user_id = ? ";

        String[] whereArgs = new String[]{
                KEY_USER_ID
        };

        Cursor cursor = db.rawQuery(queryString, whereArgs);
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnIndexOrThrow(KEY_USER_ID);
            } while (cursor.moveToNext());
        }
        String[] whereArg = new String[]{userInfo.getUserId()};

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, userInfo.getFirstName());
        values.put(KEY_LAST_NAME, userInfo.getLastName());
        values.put(KEY_EMAIL_ID, userInfo.getEmailId());
        values.put(KEY_PHONE_NO, userInfo.getPhoneNo());
        values.put(KEY_ADDRESS, userInfo.getAddress());
        db.update(TABLE_USER_INFO, values, "user_id = ? ", whereArg);
    }

    /**
     * @param policyList
     */
    public void insertPolicyList(List<PolicyInfo> policyList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (PolicyInfo policy : policyList) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, policy.getUserId());
            values.put(KEY_POLICY_ID, policy.getPolicyId());
            values.put(KEY_POLICY_NO, policy.getPolicyNo());
            values.put(KEY_MAKE, policy.getMake());
            values.put(KEY_MODEL, policy.getModel());
            values.put(KEY_COLOR, policy.getColor());
            values.put(KEY_LICENSE_NO, policy.getLicenseNo());
            values.put(KEY_VIN, policy.getVin());
            db.insert(TABLE_POLICY_INFO, null, values);
        }
        db.close();
        Log.i(TAG, "Finish inserting " + policyList.size() + " records into Table 'TABLE_POLICY_INFO'!");
    }

    /**
     * @param claim
     */
    public void insertClaim(Claim claim) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, claim.getUserId());
        values.put(KEY_POLICY_ID, claim.getPolicyId());
        values.put(KEY_CLAIM_ID, claim.getClaimId());
        values.put(KEY_STATUS, claim.getStatus());
        values.put(KEY_LONGITUDE, claim.getLongitude());
        values.put(KEY_LATITUDE, claim.getLatitude());
        values.put(KEY_SUBMITTED_TIME, claim.getSubmittedTime());
        db.insert(TABLE_CLAIMS, null, values);
        db.close();
        Log.i(TAG, "Finishing inserting Claim: " + claim.getClaimId() + "!");
    }

    /**
     * @param claimsList
     */
    public boolean insertClaimList(List<Claim> claimsList) {
        boolean isSuccessful = true;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            for (Claim claim : claimsList) {
                ContentValues values = new ContentValues();
                values.put(KEY_USER_ID, claim.getUserId());
                values.put(KEY_POLICY_ID, claim.getPolicyId());
                values.put(KEY_CLAIM_ID, claim.getClaimId());
                values.put(KEY_STATUS, claim.getStatus());
                values.put(KEY_LONGITUDE, claim.getLongitude());
                values.put(KEY_LATITUDE, claim.getLatitude());
                values.put(KEY_SUBMITTED_TIME, claim.getSubmittedTime());
                db.insert(TABLE_CLAIMS, null, values);
            }
            db.close();
            Log.i(TAG, "Finish inserting " + claimsList.size() + " records into Table 'TABLE_CLAIMS'!");
        } catch (Exception ex) {
            isSuccessful = false;
        }
        return isSuccessful;
    }

    public ArrayList<Claim> getClaimsDetails(String userID) {

        String selectQuery = "SELECT  * FROM " + TABLE_CLAIMS + " WHERE " + KEY_USER_ID + " = '" + userID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Claim> claimsList = new ArrayList<Claim>();
        if (cursor.moveToFirst()) {
            do {
                Claim claim = new Claim();
                claim.setUserId(cursor.getString(0));
                claim.setPolicyId(cursor.getString(1));
                claim.setClaimId(cursor.getString(2));
                claim.setStatus(cursor.getString(3));
                claim.setLongitude(cursor.getDouble(4));
                claim.setLatitude(cursor.getDouble(5));
                claim.setSubmittedTime(cursor.getString(6));
                claimsList.add(claim);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "Get " + claimsList.size() + " claims.");
        return claimsList;
    }

    public ArrayList<Claim> getClaimsPoliciesDetails(String userID) {
        Calendar calendar = Calendar.getInstance();

        String selectQuery = "SELECT  * FROM " + TABLE_CLAIMS + " c INNER JOIN " + TABLE_POLICY_INFO + " p ON c." + KEY_POLICY_ID + "=p." + KEY_POLICY_ID + " WHERE c." + KEY_USER_ID + " = '" + userID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Claim> claimsList = new ArrayList<Claim>();
        if (cursor.moveToFirst()) {
            do {
                Claim claim = new Claim();
                claim.setUserId(cursor.getString(0));
                claim.setPolicyId(cursor.getString(1));
                claim.setClaimId(cursor.getString(2));
                claim.setStatus(cursor.getString(3));
                claim.setLongitude(cursor.getDouble(4));
                claim.setLatitude(cursor.getDouble(5));
                claim.setSubmittedTime(cursor.getString(6));
                claim.setPolicyId(cursor.getString(cursor.getColumnIndex(KEY_POLICY_ID)));
                claim.setPolicyNumber(cursor.getString(cursor.getColumnIndex(KEY_POLICY_NO)));
                claim.setMake(cursor.getString(cursor.getColumnIndex(KEY_MAKE)));
                claim.setModel(cursor.getString(cursor.getColumnIndex(KEY_MODEL)));
                claim.setColor(cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
                claim.setLicenseNumber(cursor.getString(cursor.getColumnIndex(KEY_LICENSE_NO)));
                claim.setVinNumber(cursor.getString(cursor.getColumnIndex(KEY_VIN)));
                claimsList.add(claim);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "Get " + claimsList.size() + " claims.");
        return claimsList;
    }

    /**
     * create insert image into Table "TABLE_IMAGE_DETAILS"
     *
     * @param imageList
     * @param claimID
     */
    public void insertImageDetails(List<Image> imageList, String claimID) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Image image : imageList) {
            ContentValues values = new ContentValues();
            values.put(KEY_CLAIM_ID, claimID);
            values.put(KEY_IMAGE_ID, UUID.randomUUID().toString());
            values.put(KEY_IMAGE_PATH, image.getImageURI());
            values.put(KEY_IMAGE_TYPE, image.getImageType());
            db.insert(TABLE_IMAGE_DETAILS, null, values);
        }
        db.close();
        Log.i(TAG, "Finish inserting " + imageList.size() + " images into Table 'TABLE_IMAGE_DETAILS'!");
    }

    public ArrayList<Image> getImageDetails(String claimId) {
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE_DETAILS + " WHERE " + KEY_CLAIM_ID + " = '" + claimId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Image> imageArrayList = new ArrayList<Image>();
        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();
                image.setClaimId(cursor.getString(0));
                image.setImageId(cursor.getString(1));
                image.setImageURI(cursor.getString(2));
                image.setImageType(cursor.getString(3));
                imageArrayList.add(image);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "Get " + imageArrayList.size() + " claims.");
        return imageArrayList;
    }


    public void insertDataIntoDB(JSONObject object) {
        try {
            JSONArray data = object.getJSONArray("data");
            String[] args = new String[data.length()];

            // prepare SQL insert
            StringBuilder SQL = new StringBuilder("insert into ");
            SQL.append(object.getString("tablename")).append(" (");
            SQL.append(data.getJSONObject(0).getString("column"));
            args[0] = data.getJSONObject(0).getString("value");
            for (int i = 1; i < data.length(); i++) {
                SQL.append(", ").append(
                        data.getJSONObject(i).getString("column"));
                args[i] = data.getJSONObject(i).getString("value");
            }
            SQL.append(") values (?");
            for (int i = 0; i < data.length() - 1; i++) {
                SQL.append(", ?");
            }
            SQL.append(")");
            Log.i("inserting_stmt", SQL.toString());
            String line = "";
            for (String str : args) {
                line += " " + str;
            }
            Log.i("inserting_value", line);
            this.getWritableDatabase().execSQL(SQL.toString(), args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDataIntoDB(JSONObject object) {
        try {
            JSONArray data = object.getJSONArray("data");
            JSONArray where = object.getJSONArray("where");
            String[] args = new String[data.length() + where.length()];
            int count = 0;

            // prepare SQL update statement
            StringBuilder SQL = new StringBuilder("update ");
            SQL.append(object.getString("tablename")).append(" set ");
            SQL.append(data.getJSONObject(0).getString("column")).append(
                    " = ? ");
            args[count++] = data.getJSONObject(0).getString("value");
            for (int i = 1; i < data.length(); i++) {
                SQL.append(", ").append(
                        data.getJSONObject(i).getString("column") + " = ? ");
                args[count++] = data.getJSONObject(i).getString("value");
            }
            SQL.append(" where ")
                    .append(where.getJSONObject(0).getString("column"))
                    .append(" = ? ");
            args[count++] = where.getJSONObject(0).getString("value");
            for (int i = 1; i < where.length(); i++) {
                SQL.append(" and ")
                        .append(where.getJSONObject(i).getString("column"))
                        .append(" = ? ");
                args[count++] = where.getJSONObject(i).getString("value");
            }
            //SQL.append(")");
            Log.i("update_stmt", SQL.toString());
            String line = "";
            for (String str : args) {
                line += " " + str;
            }
            Log.i("update_value", line);
            this.getWritableDatabase().execSQL(SQL.toString(), args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteDataFromDB(JSONObject object) {
        try {
            JSONArray data = object.getJSONArray("data");
            String[] args = new String[data.length()];

            // prepare SQL delete statement
            StringBuilder SQL = new StringBuilder("delete from ");
            SQL.append(object.getString("tablename")).append(" where ");
            args[0] = data.getJSONObject(0).getString("value");
            SQL.append(data.getJSONObject(0).getString("column")).append(
                    " = ? ");
            for (int i = 1; i < data.length(); i++) {
                SQL.append(" and ")
                        .append(data.getJSONObject(i).getString("column"))
                        .append(" = ? ");
                args[i] = data.getJSONObject(i).getString("value");
            }
            Log.i("delete_stmt", SQL.toString());
            String line = "";
            for (String str : args) {
                line += " " + str;
            }
            Log.i("delete_value", line);
            this.getWritableDatabase().execSQL(SQL.toString(), args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void queryDataFromDB(JSONObject object) {
        try {
            JSONArray where = object.getJSONArray("data");
            String[] args = new String[where.length()];
            int count = 0;

            // prepare SQL query statement
            StringBuilder SQL = new StringBuilder("select * from ");
            SQL.append(object.getString("tablename"));
            SQL.append(" where ")
                    .append(where.getJSONObject(0).getString("column"))
                    .append(" = ? ");
            args[0] = where.getJSONObject(0).getString("value");
            for (int i = 1; i < where.length(); i++) {
                SQL.append(" and ")
                        .append(where.getJSONObject(i).getString("column"))
                        .append(" = ? ");
                args[count++] = where.getJSONObject(i).getString("value");
            }
            //SQL.append(")");
            Log.i("query_stmt", SQL.toString());
            String line = "";
            for (String str : args) {
                line += " " + str;
            }
            Log.i("query_value", line);
            this.getWritableDatabase().execSQL(SQL.toString(), args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
