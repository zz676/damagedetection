package com.aig.science.damagedetection.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.PolicyListAdapter;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.utilities.SessionManager;

public class PolicyListActivity extends Activity implements OnItemClickListener {


    public final static String POLICY_KEY = "com.aig.science.damagedetection.controllers.PolicyListActivity.policyKey";
    private ListView policyDetailListView;
    private ArrayList<PolicyInfo> policyDetailList;
    private PolicyListAdapter adapter;
    private String isTripStatus = "pending";
    private SessionManager session;
    private List<String> logInInfo;
    public final static String TAG = "POLICYLISTACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_policy_list);
        initUI();
        //Bundle extra we come to this activity.
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        if (logInInfo.get(0) == null || logInInfo.get(1) == null) {
            Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
        new GetPolicies(this).execute();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        new GetPolicies(this).execute();
    }


    private void fetchPolicies() {

        DatabaseHelper dbHelper = new DatabaseHelper(PolicyListActivity.this);
        policyDetailList = dbHelper.getPolicyDetails(logInInfo.get(0));

    }

    private void initUI() {

        policyDetailListView = (ListView) findViewById(R.id.policy_detail_list_id);
        policyDetailListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Intent intent = new Intent(this, PolicyInfoActivity.class);
        intent.putExtra(POLICY_KEY, policyDetailList.get(position));
        intent.putExtra("ACTION", "OPEN");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_policy_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_policy_id:
                Intent intent = new Intent(this, PolicyInfoActivity.class);
                intent.putExtra("ACTION","NEW");
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetPolicies extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private Context context;

        public GetPolicies(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(Void args0) {
            super.onPostExecute(args0);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //android.os.Debug.waitForDebugger();
            Collections.sort(policyDetailList);
            if (policyDetailList != null && policyDetailList.size() > 0) {
                adapter = new PolicyListAdapter(context, policyDetailList, isTripStatus);
                policyDetailListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("There are currently no Policies.");
                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                //builder.show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //android.os.Debug.waitForDebugger();
            fetchPolicies();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(getBaseContext(), HomeScreenActivity.class);
        startActivity(homeIntent);
    }
}
