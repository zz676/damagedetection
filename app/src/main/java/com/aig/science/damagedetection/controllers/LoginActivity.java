package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.models.Image;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.models.UserInfo;
import com.aig.science.damagedetection.services.ServiceHandler;
import com.aig.science.damagedetection.utilities.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements OnClickListener {

    private static final String TAG = "LOGINACTIVITY";
    private final static String USERNAME = "com.aig.science.damagedetection.USERNAME";
    private final static String PASSWORD = "com.aig.science.damagedetection.PASSWORD";
    private static final String PREF_NAME = "com.aig.science.damagedetection.SHAREDPREFERENCE";
    // used to make sure the first time the user logs into system, the authentication will be done
    // through cloud.
    private final static String ISFIRSTLOGININ = "com.aig.science.damagedetection.ISFIRSTLOGININ";
    private static SharedPreferences sharePreferences;
    private SessionManager session;
    private View focusView = null;
    private EditText userNameEditText, passwordEditText;
    private Button loginBtn, cancelBtn;
    private String userNameStr, passwordStr;

    private UserInfo userInfo;
    private boolean isFirstLoginIn = false;
    private TextView signUpTxtview;
    private String userID;
    private DatabaseHelper dbHelper;
    private ProgressDialog checkCredentialDialog;
    private ServiceHandler handle;

    private List<Claim> claimList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        session = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        initUI();
    }

    private void initUI() {
        userNameEditText = (EditText) findViewById(R.id.userNameEditTextID);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTextID);
        loginBtn = (Button) findViewById(R.id.loginBtnID);
        cancelBtn = (Button) findViewById(R.id.cancelBtnID);
        loginBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        signUpTxtview = (TextView) findViewById(R.id.sign_up_txtview);
        signUpTxtview.setMovementMethod(LinkMovementMethod.getInstance());
        signUpTxtview.setPaintFlags(signUpTxtview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUpTxtview.setOnClickListener(this);
    }

    /**
     * This method displays the dialog box with the title and message
     *
     * @param title
     * @param message
     */
    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginBtnID:
                checkUserInput();
                Intent homeIntent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                //boolean isLogInSuccessLocal = checkCredentialsLocal();
                //boolean isLogInSuccessLocal = false;

                //sharePreferences = getSharedPreferences(PREF_NAME, 0);
                //SharedPreferences.Editor editor = sharePreferences.edit();
                //editor.putBoolean(ISFIRSTLOGININ, false);
               if (!checkCredentialsLocal()) {
                    checkCredentialDialog = new ProgressDialog(this);
                    checkCredentialDialog.setCancelable(false);
                    checkCredentialDialog.setMessage("Logging into your account..");
                    checkCredentialDialog.isIndeterminate();
                    checkCredentialDialog.show();
                    checkCredentialsRemote();
                } else {
                    userID = dbHelper.getUserID(userNameStr, passwordStr);
                    session.createLoginSession(userID, userNameStr, passwordStr);
                    startActivity(homeIntent);
                }
                /*if (checkCredentialsLocal()) {
                    userID = dbHelper.getUserID(userNameStr, passwordStr);
                    session.createLoginSession(userID, userNameStr, passwordStr);
                    startActivity(homeIntent);
                } else {
                    //checkCredentialDialog.dismiss();
                    showDialog("Opps!!",
                            "Invalid Credentials. \n Please try again");
                }*/
                break;
            case R.id.cancelBtnID:
                //moveTaskToBack(true);
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
            case R.id.sign_up_txtview:
                final Intent regiIntent = new Intent(this, RegistrationActivity.class);
                startActivity(regiIntent);
                break;
        }
    }

    private boolean checkCredentialsLocal() {
        return dbHelper.validateUser(userNameStr, passwordStr);
    }

    private boolean checkCredentialsRemote() {
        sharePreferences = getSharedPreferences(PREF_NAME, 0);
        handle = new ServiceHandler(this);
        JSONObject userInfoJSON = userInfo.toQueryJSON(userInfo);
        System.out.println(userInfoJSON.toString());
        if (userInfoJSON != null) {
            handle.startServices(TAG, this, userInfoJSON.toString());
        }
        return true;
    }

    public void handleRemoteAuthResponse(JSONArray resultArray) {
        List<UserInfo> usersList = new ArrayList<>();
        String tableName = "";
        try {
            if (resultArray != null && resultArray.length() > 0) {
                UserInfo tempUserInfo;
                for (int i = 0; i < resultArray.length(); i++) {
                    tempUserInfo = new UserInfo();
                    tempUserInfo.setUserId(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_USER_ID));
                    tempUserInfo.setFirstName(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_FIRST_NAME));
                    tempUserInfo.setLastName(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_LAST_NAME));
                    tempUserInfo.setPhoneNo(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_PHONE_NO));
                    tempUserInfo.setEmailId(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_EMAIL_ID));
                    tempUserInfo.setUsername(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_EMAIL_ID));
                    //tempUserInfo.setUsername(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_USERNAME));
                    tempUserInfo.setPassword(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_PASSWORD));
                    tempUserInfo.setAddress(resultArray.getJSONObject(0).getString(DatabaseHelper.KEY_ADDRESS));
                    usersList.add(tempUserInfo);
                }
                userInfo = usersList.get(0);
                boolean isTableExists = dbHelper.isTableExists(dbHelper.TABLE_USER_INFO);
                dbHelper.createUserInfo(userInfo);
                checkCredentialDialog.setMessage("Log in successfully.");
                //userID = dbHelper.getUserID(userNameStr, passwordStr);
                session.createLoginSession(userInfo.getUserId(), userInfo.getUsername(), userInfo.getPassword());
                checkCredentialsLocal();
                checkCredentialDialog.setMessage("Sync your data...");
                syncDatabase(userInfo);
            } else {
                checkCredentialDialog.dismiss();
                showDialog("Opps!!",
                        "Invalid Credentials. \n Please try again");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (checkCredentialDialog != null) {
                checkCredentialDialog.dismiss();
            }
        }
    }

    /**
     * Syn the local database with the remote database
     *
     * @return
     */
    public boolean syncDatabase(UserInfo userInfo) {

        JSONObject policyJSON = PolicyInfo.toQueryJSON(userInfo);
        System.out.println(policyJSON.toString());
        handle.startServices(PolicyListActivity.TAG, this, policyJSON.toString());
        return false;
    }

    /**
     * @param resultArray
     */
    public void syncPolicyInfoTable(JSONArray resultArray) {

        List<PolicyInfo> policiesList = new ArrayList<>();
        String tableName = "";
        try {
            if (resultArray.length() > 0) {
                PolicyInfo tempPolicyInfo;
                for (int i = 0; i < resultArray.length(); i++) {
                    tempPolicyInfo = new PolicyInfo();
                    tempPolicyInfo.setUserId(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_USER_ID));
                    tempPolicyInfo.setPolicyId(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_POLICY_ID));
                    tempPolicyInfo.setPolicyNo(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_POLICY_NO));
                    tempPolicyInfo.setMake(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_MAKE));
                    tempPolicyInfo.setModel(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_MODEL));
                    tempPolicyInfo.setColor(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_COLOR));
                    tempPolicyInfo.setLicenseNo(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_LICENSE_NO));
                    tempPolicyInfo.setVin(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_VIN));
                    policiesList.add(tempPolicyInfo);
                }
                dbHelper.insertPolicyList(policiesList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (checkCredentialDialog != null) {
                checkCredentialDialog.dismiss();
            }
        }
    }

    /**
     * @return
     */
    public boolean startSyncClaimsService() {

        JSONObject claimJSON = Claim.toQueryJSON(userInfo);
        System.out.println(claimJSON.toString());
        handle.startServices(ClaimsListActivity.TAG, this, claimJSON.toString());
        return false;
    }

    /**
     * @param resultArray
     */
    public void syncClaimsTable(JSONArray resultArray) {
        claimList = new ArrayList<>();
        String tableName = "";
        try {
            if (resultArray.length() > 0) {
                Claim tempClaim;
                for (int i = 0; i < resultArray.length(); i++) {
                    tempClaim = new Claim();
                    tempClaim.setUserId(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_USER_ID));
                    tempClaim.setPolicyId(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_POLICY_ID));
                    tempClaim.setClaimId(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_CLAIM_ID));
                    tempClaim.setStatus(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_STATUS));
                    tempClaim.setLongitude(resultArray.getJSONObject(i).getDouble(DatabaseHelper.KEY_LONGITUDE));
                    tempClaim.setLatitude(resultArray.getJSONObject(i).getDouble(DatabaseHelper.KEY_LATITUDE));
                    tempClaim.setSubmittedTime(resultArray.getJSONObject(i).getString(DatabaseHelper.KEY_SUBMITTED_TIME));
                    claimList.add(tempClaim);
                }
                dbHelper.insertClaimList(claimList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (checkCredentialDialog != null) {
                checkCredentialDialog.dismiss();
            }
        }
    }

    public boolean startSyncImagesService() {

        JSONObject imageJSON = Image.toQueryJSON(claimList);
        System.out.println(imageJSON.toString());
        handle.startServices(TakePhotosActivity.TAG, this, imageJSON.toString());
        return false;
    }

    public boolean syncImagesTable(List<Claim> claimList) {

        JSONObject imageJSON = Image.toQueryJSON(claimList);
        System.out.println(imageJSON.toString());
        handle.startServices(TakePhotosActivity.TAG, this, imageJSON.toString());
        return false;
    }

    /**
     * navigate to Home Screen
     */
    public void startHomeIntent() {
        if (checkCredentialDialog != null) {
            checkCredentialDialog.dismiss();
        }
        Intent homeIntent = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(homeIntent);
    }

    private void checkUserInput() {
        userInfo = new UserInfo();
        userNameStr = userNameEditText.getText().toString();
        passwordStr = passwordEditText.getText().toString();
        if (userNameStr != null && TextUtils.isEmpty(userNameStr)) {
            userNameEditText.setError(getString(R.string.error_field_required));
            focusView = userNameEditText;
            return;
        } else if (TextUtils.isEmpty(passwordStr)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            return;
        }
        userInfo.setUsername(userNameStr);
        userInfo.setPassword(passwordStr);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public List<Claim> getClaimList() {
        return claimList;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setCancelable(false)
                .setIcon(getResources().getDrawable(R.drawable.ic_alert_error))
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                session.clearSession();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
