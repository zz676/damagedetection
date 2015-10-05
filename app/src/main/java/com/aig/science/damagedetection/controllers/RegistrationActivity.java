package com.aig.science.damagedetection.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.logger.Log;
import com.aig.science.damagedetection.models.UserInfo;
import com.aig.science.damagedetection.services.ServiceHandler;

import org.json.JSONObject;

public class RegistrationActivity extends Activity implements OnClickListener {

    private static final String TAG = "REGISTRATIONACTIVITY";
    UserInfo userInfo;
    private View focusView = null;
    private EditText firstNameEditText, lastNameEditText, phoneNoEditText,
            addressEditText, emailIdEditText;
    private Button submitBtn, cancelBtn;
    private String firstNameStr, lastNameStr, phoneNoStr, addressStr,
            emailIdStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        initUI();
    }

    private void initUI() {
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditTextID);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditTextID);
        phoneNoEditText = (EditText) findViewById(R.id.phoneNoEditTextID);
        addressEditText = (EditText) findViewById(R.id.addressEditTextID);
        emailIdEditText = (EditText) findViewById(R.id.emailIDEditTextID);
        submitBtn = (Button) findViewById(R.id.submitBtnID);
        cancelBtn = (Button) findViewById(R.id.cancelBtnID);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == submitBtn) {

            if (registerUser()) {
                this.finish();
                Integer uniquePINint = generateUniquePIN();
                String uniquePIN = uniquePINint.toString();
                String body = "Thanks for registering Damage Detection\n"
                        + "Your one time registration code is " + uniquePIN;
                sendMail(emailIdStr, "Damage Detection", body);

                Intent main_Intent = new Intent(RegistrationActivity.this,
                        InputRegistrationCodeActivity.class);
                main_Intent.putExtra("USER_ID", userInfo.getUserId());
                main_Intent.putExtra("UNIQUEID", uniquePIN);
                main_Intent.putExtra("EMAIL_ID", userInfo.getEmailId());
                startActivity(main_Intent);
            } else {
                showDialog("Opps!!",
                        "There was a problem while registrating user.");
            }
        }
        if (v == cancelBtn) {
            this.finish();
        }
    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody,
                    session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject,
                                  String messageBody, Session session) throws MessagingException,
            UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("damagedetection.aig@gmail.com", "AIG"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("damagedetection.aig", "admin_aig");
            }
        });
    }

    /**
     * generate unique registration code
     *
     * @return
     */
    private int generateUniquePIN() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
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
            ServiceHandler handle = new ServiceHandler(this);
            JSONObject userInfoJSON = userInfo.toInsertJSON(userInfo);
            System.out.println(userInfoJSON.toString());
            if (userInfoJSON != null) {
                handle.startServices(TAG, null, userInfoJSON.toString());
                return true;
            }
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

    /**
     * @return
     */
    private UserInfo createUserAccount() {
        try {
            firstNameStr = firstNameEditText.getText().toString();
            lastNameStr = lastNameEditText.getText().toString();
            phoneNoStr = phoneNoEditText.getText().toString();
            addressStr = addressEditText.getText().toString();
            emailIdStr = emailIdEditText.getText().toString();
            if (firstNameStr.length() > 0 && lastNameStr.length() > 0
                    && phoneNoStr.length() > 0 && addressStr.length() > 0
                    && emailIdStr.length() > 0) {
                userInfo = new UserInfo();
                //userInfo.setUserId("U" + phoneNoStr);
                userInfo.setUserId(generateUserID());

                userInfo.setFirstName(firstNameStr);
                userInfo.setLastName(lastNameStr);
                userInfo.setPhoneNo(phoneNoStr);
                userInfo.setAddress(addressStr);
                userInfo.setEmailId(emailIdStr);
                return userInfo;
            }
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
        }
        return null;
    }

    /**
     * generate random and unique string for user_id
     *
     * @return
     */
    private String generateUserID() {
        return UUID.randomUUID().toString();
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//			progressDialog = ProgressDialog.show(RegistrationActivity.this,
//					"Please wait", "Sending registration code to email provided", false, true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //android.os.Debug.waitForDebugger();
            super.onPostExecute(aVoid);
//			progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            //android.os.Debug.waitForDebugger();
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
