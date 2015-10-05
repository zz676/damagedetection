package com.aig.science.damagedetection.controllers;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Constants;
import com.aig.science.damagedetection.models.PolicyMaster;

/** used the below link for animation **/

/**
 * http://www.androidhive.info/2013/06/android-working-with-xml-animations/  *
 */

public class SplashScreenActivity extends Activity implements AnimationListener {
    /**
     * Called when the activity is first created.
     */
    private static final String LOGTAG = "SplashScreenActivity";
    private long _splashTime = 2000;
    //private long _splashTime = 100;
    public static Context contextOfApplication;
    ImageView ethTxt;
    // Animation
    Animation animFadeIn, animFadeOut;
    SharedPreferences sharedPreferences_put;
    SharedPreferences.Editor editor;
    private boolean HaveConnectedWifi = false;
    private boolean HaveConnectedMobile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        contextOfApplication = getApplicationContext();

        ethTxt = (ImageView) findViewById(R.id.eth_textId);
        // load the animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        // set animation listener
        animFadeIn.setAnimationListener(this);

        ethTxt.setVisibility(View.VISIBLE);
        ethTxt.startAnimation(animFadeIn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sharedPreferences_put = getSharedPreferences(Constants.User_Setting_file, Activity.MODE_PRIVATE);
                Boolean isRegistered = sharedPreferences_put.getBoolean(Constants.IS_REGISTERED, false);
                Intent main_Intent = null;
                if (isRegistered)
                    main_Intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                else {
                    createDB();
                    main_Intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                startActivity(main_Intent);
                // animating switching of 2 activities with fadein fadeout animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            private void createDB() {
                // TODO Auto-generated method stub
                ArrayList<PolicyMaster> policyMasterList = new ArrayList<PolicyMaster>();
                PolicyMaster pb1 = new PolicyMaster("firstname1", "lastname1", "123456789", "test1@gmail.com", "175 Water street", "TOYOTA", "RAV4", "Black", "test123", "VIN12345", "POLICY11");
                PolicyMaster pb2 = new PolicyMaster("firstname2", "lastname2", "234567891", "test2@gmail.com", "176 Water street", "TOYOTA", "CAMRY", "RED", "test234", "VIN23456", "POLICY21");
                PolicyMaster pb3 = new PolicyMaster("firstname3", "lastname3", "34567891", "test3@gmail.com", "125 Water street", "HYUNDAI", "Veloster", "Black", "test333", "VIN34567", "POLICY31");
                PolicyMaster pb4 = new PolicyMaster("firstname1", "lastname1", "123456789", "test4@gmail.com", "175 Water street", "HYUNDAI", "ELANTRA", "RED", "test987", "VIN98765", "POLICY12");
                policyMasterList.add(pb1);
                policyMasterList.add(pb2);
                policyMasterList.add(pb3);
                policyMasterList.add(pb4);
                DatabaseHelper dbHelper = new DatabaseHelper(
                        SplashScreenActivity.this);
                Iterator<PolicyMaster> iterator = policyMasterList.iterator();
                while (iterator.hasNext()) {
                    PolicyMaster policyMaster = iterator.next();
                    dbHelper.createPolicyMaster(policyMaster);
                }
                dbHelper.close();
            }
        }, _splashTime);
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
        builder.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation
        // check for fade in animation
        if (animation == animFadeIn) {
            ethTxt.startAnimation(animFadeOut);
        }
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


}
