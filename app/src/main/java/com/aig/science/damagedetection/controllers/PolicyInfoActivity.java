package com.aig.science.damagedetection.controllers;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Constants;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.services.ServiceHandler;
import com.aig.science.damagedetection.utilities.SessionManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.security.Policy;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class PolicyInfoActivity extends Activity implements OnClickListener {

    private static final String TAG = "POLICYINFOACTIVITY";
    private SharedPreferences sharedPreferences_put;
    private SharedPreferences.Editor editor;
    private EditText policyNoEditText, makeEditText, modelEditText, colorEditText, typeEditText, licenseNoEditText, vinEditText;
    private Button submitBtn, cancelBtn;
    private String userIDStr, policyIDStr, policyNoStr, makeStr, modelStr, colorStr, typeStr, licenseNoStr, vinStr;
    private View focusView = null;
    private SessionManager session;
    private String isEditMode = "NO";
    private List<String> logInInfo;
    private MenuItem editMenuItem;
    private MenuItem deleteMenuItem;
    private PolicyInfo currentPolicyInfo;
    private PolicyInfo selectedPolicyInfo;
    private DatabaseHelper dbHelper;
    private String action;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policyinfo);
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        initUI();
        Intent intent = getIntent();
        dbHelper = new DatabaseHelper(this);
        if (intent != null) {
            action = intent.getStringExtra("ACTION");
            if (action != null) {
                if (action.equals("OPEN")) {
                    action = "OPEN";
                    //selectedPolicyInfo = (PolicyInfo) intent.getSerializableExtra("policy");
                    selectedPolicyInfo = intent.getParcelableExtra(PolicyListActivity.POLICY_KEY);
                    fetchView(selectedPolicyInfo);
                } else if (action.equals("NEW")) {
                    action = "NEW";
                    submitBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    policyNoEditText.setEnabled(true);
                    makeEditText.setEnabled(true);
                    modelEditText.setEnabled(true);
                    colorEditText.setEnabled(true);
                    vinEditText.setEnabled(true);
                    licenseNoEditText.setEnabled(true);
                }
            }
        }
    }

    /**
     * init UI
     */
    private void initUI() {

        policyNoEditText = (EditText) findViewById(R.id.policyNoEditTextID);
        makeEditText = (EditText) findViewById(R.id.makeEditTextID);
        modelEditText = (EditText) findViewById(R.id.modelEditTextID);
        colorEditText = (EditText) findViewById(R.id.colorEditTextID);
        licenseNoEditText = (EditText) findViewById(R.id.licenseNoEditTextID);
        vinEditText = (EditText) findViewById(R.id.vinEditTextID);
        policyNoEditText.setEnabled(false);
        makeEditText.setEnabled(false);
        modelEditText.setEnabled(false);
        colorEditText.setEnabled(false);
        vinEditText.setEnabled(false);
        licenseNoEditText.setEnabled(false);

        submitBtn = (Button) findViewById(R.id.submitBtnID);
        cancelBtn = (Button) findViewById(R.id.cancelBtnID);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        submitBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * @param selectedPolicyInfo
     */
    private void fetchView(PolicyInfo selectedPolicyInfo) {

        String policyNo = selectedPolicyInfo.getPolicyNo().toString();
        String make = selectedPolicyInfo.getMake().toString();
        String model = selectedPolicyInfo.getModel().toString();
        String color = selectedPolicyInfo.getColor().toString();
        String licenseNo = selectedPolicyInfo.getLicenseNo().toString();
        String vin = selectedPolicyInfo.getVin().toString();
        userIDStr = selectedPolicyInfo.getUserId().toString();
        policyIDStr = selectedPolicyInfo.getPolicyId().toString();
        policyNoEditText.setText(policyNo);
        makeEditText.setText(make);
        modelEditText.setText(model);
        colorEditText.setText(color);
        licenseNoEditText.setText(licenseNo);
        vinEditText.setText(vin);
    }

    @Override
    public void onClick(View v) {

        if (v == submitBtn) {
            if (isEditMode.equalsIgnoreCase("YES")) {
                boolean isPolicyEdited = editPolicy();
                if (isPolicyEdited) {
                    policyNoEditText.setEnabled(false);
                    makeEditText.setEnabled(false);
                    modelEditText.setEnabled(false);
                    colorEditText.setEnabled(false);
                    vinEditText.setEnabled(false);
                    licenseNoEditText.setEnabled(false);
                    editMenuItem.setVisible(true);
                    deleteMenuItem.setVisible(true);
                    submitBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                }
            } else {
                boolean isPolicyCreated = createPolicy();
                if (isPolicyCreated) {
                    this.finish();
                    Intent main_Intent = new Intent(PolicyInfoActivity.this, PolicyListActivity.class);
                    startActivity(main_Intent);
                }
            }
        }

        if (v == cancelBtn) {
            if (isEditMode.equalsIgnoreCase("YES")) {
                editMenuItem.setVisible(true);
                deleteMenuItem.setVisible(true);
                submitBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                policyNoEditText.setEnabled(false);
                makeEditText.setEnabled(false);
                modelEditText.setEnabled(false);
                colorEditText.setEnabled(false);
                vinEditText.setEnabled(false);
                licenseNoEditText.setEnabled(false);
                fetchView(selectedPolicyInfo);
            } else {
                this.finish();
            }
        }
    }

    private boolean editPolicy() {
        PolicyInfo editedPolicyInfo = createEditPolicyDetailObj();
        selectedPolicyInfo = editedPolicyInfo;
        if (checkUserInput() && editedPolicyInfo != null) {
            ServiceHandler handle = new ServiceHandler(this);
            JSONObject editPolicyInfoJSON = editedPolicyInfo.toUpdateJSON(editedPolicyInfo);
            if (editPolicyInfoJSON != null) {
                handle.startServices(TAG, null, editPolicyInfoJSON.toString());
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private PolicyInfo createEditPolicyDetailObj() {

        PolicyInfo policyInfo = null;
        policyNoStr = policyNoEditText.getText().toString();
        policyIDStr = selectedPolicyInfo.getPolicyId();
        makeStr = makeEditText.getText().toString();
        modelStr = modelEditText.getText().toString();
        colorStr = colorEditText.getText().toString();
        licenseNoStr = licenseNoEditText.getText().toString();
        vinStr = vinEditText.getText().toString();
        //typeStr = typeEditText.getText().toString();
        if (policyNoStr.length() > 0 && makeStr.length() > 0
                && modelStr.length() > 0 && colorStr.length() > 0
                && licenseNoStr.length() > 0 && vinStr.length() > 0) {
            policyInfo = new PolicyInfo();
            policyInfo.setUserId(userIDStr);
            policyInfo.setPolicyId(policyIDStr);
            policyInfo.setPolicyNo(policyNoStr);
            policyInfo.setMake(makeStr);
            policyInfo.setModel(modelStr);
            policyInfo.setColor(colorStr);
            policyInfo.setLicenseNo(licenseNoStr);
            policyInfo.setVin(vinStr);
            return policyInfo;
        }
        return null;
    }

    private boolean createPolicy() {

        PolicyInfo newtPolicyInfo = createPolicyDetail();
        if (checkUserInput() && newtPolicyInfo != null) {
            ServiceHandler handle = new ServiceHandler(this);
            JSONObject newPolicyInfoJSON = newtPolicyInfo.toInsertJSON(newtPolicyInfo);
            if (newPolicyInfoJSON != null) {
                handle.startServices(TAG, null, newPolicyInfoJSON.toString());
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private PolicyInfo createPolicyDetail() {
        PolicyInfo policyInfo = null;
        policyNoStr = policyNoEditText.getText().toString();
        makeStr = makeEditText.getText().toString();
        modelStr = modelEditText.getText().toString();
        colorStr = colorEditText.getText().toString();
        licenseNoStr = licenseNoEditText.getText().toString();
        vinStr = vinEditText.getText().toString();
        //typeStr = typeEditText.getText().toString();
        if (policyNoStr.length() > 0 && makeStr.length() > 0
                && modelStr.length() > 0 && colorStr.length() > 0
                && licenseNoStr.length() > 0 && vinStr.length() > 0) {
            policyInfo = new PolicyInfo();
            //sharedPreferences_put = getSharedPreferences(Constants.User_Setting_file, Activity.MODE_PRIVATE);
            //String userID = sharedPreferences_put.getString(Constants.USER_ID, "");
            policyInfo.setUserId(logInInfo.get(0));
            policyInfo.setPolicyId(UUID.randomUUID().toString());
            policyInfo.setPolicyNo(policyNoStr);
            policyInfo.setMake(makeStr);
            policyInfo.setModel(modelStr);
            policyInfo.setColor(colorStr);
            policyInfo.setLicenseNo(licenseNoStr);
            policyInfo.setVin(vinStr);
            return policyInfo;
        }
        return null;
    }

    private boolean checkUserInput() {
        if (policyNoStr != null && TextUtils.isEmpty(policyNoStr)) {
            policyNoEditText.setError(getString(R.string.error_field_required));
            focusView = policyNoEditText;
            return false;
        } else if (makeStr != null && TextUtils.isEmpty(makeStr)) {
            makeEditText.setError(getString(R.string.error_field_required));
            focusView = makeEditText;
            return false;
        } else if (modelStr != null && TextUtils.isEmpty(modelStr)) {
            modelEditText.setError(getString(R.string.error_field_required));
            focusView = modelEditText;
            return false;
        } else if (colorStr != null && TextUtils.isEmpty(colorStr)) {
            colorEditText.setError(getString(R.string.error_field_required));
            focusView = colorEditText;
            return false;
        } else if (licenseNoStr != null && TextUtils.isEmpty(licenseNoStr)) {
            licenseNoEditText.setError(getString(R.string.error_field_required));
            focusView = licenseNoEditText;
            return false;
        } else if (vinStr != null && TextUtils.isEmpty(vinStr)) {
            vinEditText.setError(getString(R.string.error_field_required));
            focusView = vinEditText;
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.policy_info_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        editMenuItem = menu.findItem(R.id.edit_policy);
        deleteMenuItem = menu.findItem(R.id.delete_policy);
        if (action.equalsIgnoreCase("NEW")) {
            editMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.back_to_home:
                final Intent intent = new Intent(this, HomeScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_policy:
                editMenuItem.setVisible(false);
                deleteMenuItem.setVisible(false);
                submitBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                policyNoEditText.setEnabled(true);
                makeEditText.setEnabled(true);
                modelEditText.setEnabled(true);
                colorEditText.setEnabled(true);
                vinEditText.setEnabled(true);
                licenseNoEditText.setEnabled(true);
                isEditMode = "YES";
                break;
            case R.id.delete_policy:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final Intent poliyListIntent = new Intent(this, PolicyListActivity.class);
                final ServiceHandler handle = new ServiceHandler(this);
                builder.setTitle("Warning");
                builder.setMessage("Do you want confirm this delete?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                //Boolean boolean1 = dbHelper.deletePolicy(selectedPolicyInfo.getUserId(), selectedPolicyInfo.getPolicyId());
                                Boolean isDeletedSuccessful = false;
                                JSONObject newPolicyInfoJSON = selectedPolicyInfo.toDeleteJSON(selectedPolicyInfo);
                                if (newPolicyInfoJSON != null) {
                                    handle.startServices(TAG, null, newPolicyInfoJSON.toString());
                                    isDeletedSuccessful = true;
                                }
                                if (isDeletedSuccessful) {
                                    startActivity(poliyListIntent);
                                    //dataArrayList.remove(policyInfo);
                                    showProgress();
                                }
                            }
                        });

                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                break;
        }
        return true;
    }

    public void showProgress() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        long delayInMillis = 1000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        }, delayInMillis);
    }
}
