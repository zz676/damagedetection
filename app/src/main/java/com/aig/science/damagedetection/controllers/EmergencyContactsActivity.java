package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aig.science.damagedetection.R;


public class EmergencyContactsActivity extends Activity {

    private Button dial911Btn;
    private Button hotlineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        dial911Btn = (Button)findViewById(R.id.call99_btn);
        dial911Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:911"));
                startActivity(callIntent);
            }
        });

        hotlineBtn = (Button)findViewById(R.id.hotline_btn);
        hotlineBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:1-866-960-0772"));
                startActivity(callIntent);
            }
        });
    }
}
