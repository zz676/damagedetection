package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.models.Constants;
import com.aig.science.damagedetection.models.UserInfo;
import com.aig.science.damagedetection.services.ServiceHandler;

import org.json.JSONObject;

public class SignUpActivity extends Activity implements OnClickListener {

    private static final String TAG = "CREATELOGINACTIVITY";
    SharedPreferences sharedPreferences_put;
    SharedPreferences.Editor editor;
    private EditText userNameEditText, passwordEditText, confirmPasswordEditText;
    private Button submitBtn, cancelBtn;
    private String userNameStr, passwordStr, confirmPasswordStr, userIdStr;
    UserInfo userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_login_screen);
        Intent intent = getIntent();
        if (intent != null) {
            userIdStr = (String) intent.getSerializableExtra("USER_ID");
            userNameStr = (String) intent.getSerializableExtra("EMAIL_ID");
        }
        initUI();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        userNameEditText = (EditText) findViewById(R.id.userNameEditTextID);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTextID);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditTextID);
        userNameEditText.setText(userNameStr);
        submitBtn = (Button) findViewById(R.id.submitBtnID);
        cancelBtn = (Button) findViewById(R.id.cancelBtnID);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == submitBtn) {
            createLogin();
            this.finish();
        }
        if (v == cancelBtn) {
            this.finish();
            Intent main_Intent = new Intent(SignUpActivity.this, RegistrationActivity.class);
            startActivity(main_Intent);
        }
    }

    private void createLogin() {

        passwordStr = passwordEditText.getText().toString();
        confirmPasswordStr = confirmPasswordEditText.getText().toString();
        if (passwordStr != null && confirmPasswordEditText != null
                && passwordStr.equals(confirmPasswordStr)) {

            userInfo = new UserInfo();
            userInfo.setUserId(userIdStr);
            userInfo.setUsername(userNameStr);
            userInfo.setPassword(passwordStr);

            ServiceHandler handle = new ServiceHandler(this);
            JSONObject userInfoJSON = userInfo.toUpdateJSONLogin(userInfo);
            Log.i(TAG, userInfoJSON.toString());
            if(userInfoJSON!=null)
            {
                handle.startServices(TAG,null,userInfoJSON.toString());
            }
            sharedPreferences_put = getSharedPreferences(Constants.User_Setting_file, Activity.MODE_PRIVATE);
            editor = sharedPreferences_put.edit();
            editor.putBoolean(Constants.IS_REGISTERED, true);
            editor.putString("USER_ID", userIdStr);
            editor.commit();
            this.finish();
            Intent main_Intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(main_Intent);

        } else {
            showDialog("Opps!!",
                    "Passwords do not match. \n Please re-enter the Password");
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

}
