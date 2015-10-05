package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.models.Claim;

/**
 * Created by poopatil on 11/18/2014.
 */
public class ClaimCostActivity extends Activity implements OnClickListener {

    ImageButton menuSummaryBtn,menuCostBtn,menuPhotoBtn;
    Claim claimsObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_claims_cost_screen);
        Intent intent = getIntent();
        if(intent!=null)
        {
            claimsObj= intent.getParcelableExtra(ClaimSummaryActivity.CLAIM_KEY);
        }
        initUI();
    }

    private void initUI() {
        menuSummaryBtn = (ImageButton) findViewById(R.id.footer_summary_btnId);
        menuCostBtn  = (ImageButton) findViewById(R.id.footer_cost_btnId);
        menuPhotoBtn = (ImageButton) findViewById(R.id.footer_photos_btnId);

        menuSummaryBtn.setOnClickListener(this);
        menuPhotoBtn.setOnClickListener(this);
    }




    public void onClick(View v) {

//        v.setTag("From_Main_Menu");
//        LoadClaimMenus.loadFooterMenus(v, ClaimSummaryActivity.this);
        if(v== menuSummaryBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimCostActivity.this, ClaimSummaryActivity.class);
            intent.putExtra(ClaimSummaryActivity.CLAIM_KEY, claimsObj);
            startActivity(intent);
        }
        if(v==menuPhotoBtn)
        {
            this.finish();
            Intent intent = new Intent(ClaimCostActivity.this, ClaimPhotoActivity.class);
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
}