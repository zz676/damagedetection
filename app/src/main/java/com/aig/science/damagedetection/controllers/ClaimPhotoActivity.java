package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.ClaimDetailAdapter;
import com.aig.science.damagedetection.adaptor.ImageAdaptor;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.models.Image;
import com.aig.science.damagedetection.utilities.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhisheng on 11/18/2014.
 */
public class ClaimPhotoActivity extends Activity implements OnClickListener {

    private static final String TAG = "ClaimPhotoActivity";
    private ImageButton menuSummaryBtn,menuCostBtn,menuPhotoBtn;
    private ArrayList<Image> imageArrayList;
    private DatabaseHelper dbHelper;
    private ClaimDetailAdapter adapter;
    private SessionManager session;
    private List<String> logInInfo;
    private Claim claimsObj;
    private GridView gridview;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_claims_photo_screen);
        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);
        logInInfo = session.getUserInfo();
        intent = getIntent();
        initUI();
    }

    private void initUI() {
        menuSummaryBtn = (ImageButton) findViewById(R.id.footer_summary_btnId);
        menuCostBtn  = (ImageButton) findViewById(R.id.footer_cost_btnId);
        menuPhotoBtn = (ImageButton) findViewById(R.id.footer_photos_btnId);
        menuSummaryBtn.setOnClickListener(this);
        menuCostBtn.setOnClickListener(this);
        gridview = (GridView) findViewById(R.id.gridview);
        new ReadImagesTask(this).execute();
    }

    public void onClick(View v) {

        if(v== menuSummaryBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimPhotoActivity.this, ClaimSummaryActivity.class);
            intent.putExtra(ClaimSummaryActivity.CLAIM_KEY, claimsObj);
            startActivity(intent);
        }
        if(v==menuCostBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimPhotoActivity.this, ClaimCostActivity.class);
            intent.putExtra(ClaimSummaryActivity.CLAIM_KEY, claimsObj);
            startActivity(intent);
        }
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


    private class ReadImagesTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private Context context;
        private ReadImagesTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPostExecute(Void arg) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            //android.os.Debug.waitForDebugger();
            Toast.makeText(context,"Reading images...",Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Reading images...");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //android.os.Debug.waitForDebugger();
            try{
                if(intent!=null)
                {
                    claimsObj= intent.getParcelableExtra(ClaimSummaryActivity.CLAIM_KEY);
                }
                imageArrayList =  dbHelper.getImageDetails(claimsObj.getClaimId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridview.setAdapter(new ImageAdaptor(context,imageArrayList));
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(imageArrayList.get(position).getImageURI())),"image/*");
                                startActivity(intent);
                            }
                        });
                    }
                });
            } catch (Exception ex){
                Log.d(TAG, ex.getMessage());
            }
            return null;
        }
    }
}
