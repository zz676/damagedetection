package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.models.Claim;
import android.view.View.OnClickListener;

/**
 * Created by poopatil on 11/18/2014.
 */
public class ClaimSummaryActivity extends Activity implements OnClickListener {

    protected final static String CLAIM_KEY = "com.aig.science.damagedetection.controllers.ClaimSummaryActivity.claimKey";
    private ImageButton menuSummaryBtn,menuCostBtn,menuPhotoBtn;
    private ImageView imageView;
    private TextView makeTxtView, colorTxtView,statusTxtView,policyNoTxtView, dateTimeTxtView;
    private Claim claimsObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_claims_summary);
        Intent intent = getIntent();
        if(intent!=null)
        {
           claimsObj= intent.getParcelableExtra(CLAIM_KEY);
        }
        initUI();
    }

    private void initUI() {
        menuSummaryBtn = (ImageButton) findViewById(R.id.footer_summary_btnId);
        menuCostBtn  = (ImageButton) findViewById(R.id.footer_cost_btnId);
        menuPhotoBtn = (ImageButton) findViewById(R.id.footer_photos_btnId);
        imageView = (ImageView) findViewById(R.id.claimDetail_ImageID);
        makeTxtView = (TextView) findViewById(R.id.claim_make_TextID);
        colorTxtView = (TextView) findViewById(R.id.claim_color_TextID);
        statusTxtView = (TextView) findViewById(R.id.claim_status_TextID);
        dateTimeTxtView = (TextView) findViewById(R.id.claim_datetime_TextID);
        policyNoTxtView = (TextView) findViewById(R.id.policy_claim_TextID);
        if(claimsObj!=null)
        {
            makeTxtView.setText(claimsObj.getMake() + " " + claimsObj.getModel());
            colorTxtView.setText(claimsObj.getColor());
            dateTimeTxtView.setText(claimsObj.getSubmittedTime());
            statusTxtView.setText(claimsObj.getStatus());
            policyNoTxtView.setText(claimsObj.getPolicyNumber());
        }
        menuCostBtn.setOnClickListener(this);
        menuPhotoBtn.setOnClickListener(this);
    }

    public void onClick(View v) {

        if(v==menuCostBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimSummaryActivity.this, ClaimCostActivity.class);
            intent.putExtra(CLAIM_KEY, claimsObj);
            startActivity(intent);
        }
        if(v==menuPhotoBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimSummaryActivity.this, ClaimPhotoActivity.class);
            intent.putExtra(CLAIM_KEY, claimsObj);
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
}
