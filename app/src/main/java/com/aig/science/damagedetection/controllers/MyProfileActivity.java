package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Constants;
import com.aig.science.damagedetection.models.UserInfo;
import com.aig.science.damagedetection.services.ServiceHandler;
import com.aig.science.damagedetection.utilities.SessionManager;

import org.json.JSONObject;

import java.util.List;

public class MyProfileActivity extends Activity implements OnClickListener {

    private static final String TAG = "MYPROFILEACTIVITY";
    SharedPreferences sharedPreferences_put;
    SharedPreferences.Editor editor;
    private View focusView = null;
    private EditText firstNameEditText, lastNameEditText, phoneNoEditText,
            addressEditText, emailIdEditText;
    private Button submitBtn, cancelBtn;
    private String firstNameStr, lastNameStr, phoneNoStr, addressStr,
            emailIdStr;
    private LinearLayout btnProfileLayout;
    private boolean isEdit = false;
    private UserInfo userInfo;
    private List<String> logInInfo;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        if (logInInfo.get(0) == null || logInInfo.get(1) == null) {
            Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
        initUI();
        fetchData();
    }

    private void fetchData() {
        DatabaseHelper dbHelper = new DatabaseHelper(MyProfileActivity.this);
        String userID = logInInfo.get(0);
        userInfo = dbHelper.getuserDetails(userID);
        if (userInfo != null) {
            firstNameEditText.setText(userInfo.getFirstName());
            lastNameEditText.setText(userInfo.getLastName());
            phoneNoEditText.setText(userInfo.getPhoneNo());
            addressEditText.setText(userInfo.getAddress());
            emailIdEditText.setText(userInfo.getEmailId());
        }
    }

    private void initUI() {

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditTextID);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditTextID);
        phoneNoEditText = (EditText) findViewById(R.id.phoneNoEditTextID);
        addressEditText = (EditText) findViewById(R.id.addressEditTextID);
        emailIdEditText = (EditText) findViewById(R.id.emailIDEditTextID);
        firstNameEditText.setEnabled(false);
        lastNameEditText.setEnabled(false);
        phoneNoEditText.setEnabled(false);
        addressEditText.setEnabled(false);
        emailIdEditText.setEnabled(false);

        submitBtn = (Button) findViewById(R.id.submitBtnID);
        cancelBtn = (Button) findViewById(R.id.cancelBtnID);
        if (!isEdit) {
            submitBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        }
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == submitBtn) {
            if (registerUser()) {
                if (updateUserInfo()) {
                    submitBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    firstNameEditText.setEnabled(false);
                    lastNameEditText.setEnabled(false);
                    phoneNoEditText.setEnabled(false);
                    addressEditText.setEnabled(false);
                    emailIdEditText.setEnabled(false);
                } else {
                    showDialog("Opps!!",
                            "There was a problem while updating user profile.");
                }
            } else {
                showDialog("Opps!!",
                        "There was a problem while registrating user.");
            }
        }
        if (v == cancelBtn) {
            submitBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
            firstNameEditText.setEnabled(false);
            lastNameEditText.setEnabled(false);
            phoneNoEditText.setEnabled(false);
            addressEditText.setEnabled(false);
            emailIdEditText.setEnabled(false);
            if (userInfo != null) {
                firstNameEditText.setText(userInfo.getFirstName());
                lastNameEditText.setText(userInfo.getLastName());
                phoneNoEditText.setText(userInfo.getPhoneNo());
                addressEditText.setText(userInfo.getAddress());
                emailIdEditText.setText(userInfo.getEmailId());
            }
        }
    }

    private boolean updateUserInfo() {
        userInfo = createUserInfoObject();
        if (checkUserInput() && userInfo != null) {
            ServiceHandler handle = new ServiceHandler(this);
            JSONObject userInfoJSON = userInfo.toUpdateJSON(userInfo);
            System.out.println(userInfoJSON.toString());
            if (userInfoJSON != null) {
                handle.startServices(TAG, new LoginActivity(), userInfoJSON.toString());
            }
            return true;
        } else {
            return false;
        }
    }

    private UserInfo createUserInfoObject() {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(logInInfo.get(0));
        userInfo.setFirstName(firstNameStr);
        userInfo.setLastName(lastNameStr);
        userInfo.setEmailId(emailIdStr);
        userInfo.setPhoneNo(phoneNoStr);
        userInfo.setAddress(addressStr);
        return userInfo;
    }

    private boolean checkUserInput() {
        if (firstNameStr != null && TextUtils.isEmpty(firstNameStr)) {
            firstNameEditText
                    .setError(getString(R.string.error_field_required));
            focusView = firstNameEditText;
            return false;
        } else if (TextUtils.isEmpty(lastNameStr)) {
            lastNameEditText.setError(getString(R.string.error_field_required));
            focusView = lastNameEditText;
            return false;
        } else if (!TextUtils.isDigitsOnly(phoneNoStr)) {
            phoneNoEditText.setError(getString(R.string.error_incorrect_phone));
            focusView = phoneNoEditText;
            return false;
        } else if (TextUtils.isEmpty(addressStr)) {
            addressEditText.setError(getString(R.string.error_field_required));
            focusView = addressEditText;
            return false;
        } else if (!emailIdStr.contains("@")) {
            emailIdEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailIdEditText;
            return false;
        }

        return true;
    }

    private boolean registerUser() {
        userInfo = createUserAccount();
        if (checkUserInput() && userInfo != null) {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.createUserInfo(userInfo);
            databaseHelper.close();
            return true;

        } else {
            return false;

        }
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

    private UserInfo createUserAccount() {

        firstNameStr = firstNameEditText.getText().toString();
        lastNameStr = lastNameEditText.getText().toString();
        phoneNoStr = phoneNoEditText.getText().toString();
        addressStr = addressEditText.getText().toString();
        emailIdStr = emailIdEditText.getText().toString();
        if (firstNameStr.length() > 0 && lastNameStr.length() > 0
                && phoneNoStr.length() > 0 && addressStr.length() > 0
                && emailIdStr.length() > 0) {
            userInfo = new UserInfo();
            userInfo.setUserId("U" + phoneNoStr);
            userInfo.setFirstName(firstNameStr);
            userInfo.setLastName(lastNameStr);
            userInfo.setPhoneNo(phoneNoStr);
            userInfo.setAddress(addressStr);
            userInfo.setEmailId(emailIdStr);
            return userInfo;
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.switch_edit_btn) {
            submitBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            firstNameEditText.setEnabled(true);
            lastNameEditText.setEnabled(true);
            phoneNoEditText.setEnabled(true);
            addressEditText.setEnabled(true);
            emailIdEditText.setEnabled(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}