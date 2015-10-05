package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.ClaimDetailAdapter;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.utilities.SessionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by pooja patil on 11/10/2014.
 */
public class ClaimsListActivity extends Activity implements AdapterView.OnItemClickListener {

    public final static String TAG = "ClaimsListActivity";
    private ListView claimDetailListView;
    private ArrayList<Claim> claimDetailList;
    private DatabaseHelper dbHelper;
    private ClaimDetailAdapter adapter;
    private SessionManager session;
    private List<String> logInInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_claims);
        initUI();
        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        //Bundle extra we come to this activity. 
        fetchdata();
    }

    private void fetchdata() {
        // DatabaseHelper dbHelper = new DatabaseHelper(ClaimDetailActivity.this);
        // claimDetailList = dbHelper.getPolicyDetails();
        //claimDetailList = getClaims();
        if(logInInfo.get(0)!=null){
            claimDetailList = dbHelper.getClaimsPoliciesDetails(logInInfo.get(0));
        }

        if (claimDetailList != null) {
            Collections.sort(claimDetailList);
            adapter = new ClaimDetailAdapter(this, claimDetailList);
            claimDetailListView.setAdapter(adapter);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("null data");
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    }

       private void initUI() {
        claimDetailListView = (ListView) findViewById(R.id.claims_detail_list_id);
        claimDetailListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Claim claimsObj = claimDetailList.get(position);
        Intent intent = new Intent(ClaimsListActivity.this, ClaimSummaryActivity.class);
        intent.putExtra("claimsObj", claimsObj);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_to_home, menu);
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
            default:
                break;
        }
        return true;
    }
}
