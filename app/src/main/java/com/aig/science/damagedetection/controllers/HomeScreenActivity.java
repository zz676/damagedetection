package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Constants;
import com.aig.science.damagedetection.utilities.SessionManager;

import java.util.List;

/**
 * @author Zhisheng Zhou
 */
public class HomeScreenActivity extends Activity {


    private Button emergencyContactsButton;
    private SharedPreferences sharedPreferences_put;
    private SharedPreferences.Editor editor;

    private SessionManager session;
    private List<String> logInInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        if (logInInfo.get(0) == null || logInInfo.get(1) == null) {
            Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newclaim_btn:
                final Intent newClaimIntent = new Intent(this, NewClaimCarsListActivity.class);
                startActivity(newClaimIntent);
                break;
            case R.id.emergencycontacts_btn:
                final Intent emgencyIntent = new Intent(this, EmergencyContactsActivity.class);
                emergencyContactsButton = (Button) findViewById(R.id.emergencycontacts_btn);
                startActivity(emgencyIntent);
                break;

            case R.id.newbyshops_btn:
                final Intent nearByShopsIntent = new Intent(this, NearByShopsActivity.class);
                startActivity(nearByShopsIntent);
                break;
            case R.id.myclaims_btn:
                final Intent myClaimsIntent = new Intent(this, ClaimsListActivity.class);
                startActivity(myClaimsIntent);
                // do stuff;
                break;
            case R.id.mypolicy_btn:
                sharedPreferences_put = getSharedPreferences(Constants.User_Setting_file, Activity.MODE_PRIVATE);
                String userID = sharedPreferences_put.getString(Constants.USER_ID, "");
                DatabaseHelper dbh = new DatabaseHelper(this);
                int numberOfPolicies = dbh.getNoOfPolicies(userID);
                Intent main_Intent = null;
                //if (numberOfPolicies > 0)
                main_Intent = new Intent(HomeScreenActivity.this, PolicyListActivity.class);
//                else
//                    main_Intent = new Intent(HomeScreenActivity.this, PolicyInfoActivity.class);
                startActivity(main_Intent);
                // do stuff;
                break;
            case R.id.myprofiles_btn:
                final Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
                startActivity(myProfileIntent);
                // do stuff;
                break;
            case R.id.about_aig_btn:
                final Intent aboutAIGIntent = new Intent(this, AboutAIGActivity.class);
                startActivity(aboutAIGIntent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Logout Application?");
        alertDialogBuilder
                .setCancelable(false)
                .setIcon(getResources().getDrawable(R.drawable.ic_alert_error))
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //preferences.edit().clear().commit();
                                session.logoutUser();
                                Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(loginIntent);
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
