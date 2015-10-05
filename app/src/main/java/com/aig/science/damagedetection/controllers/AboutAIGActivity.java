package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aig.science.damagedetection.R;

public class AboutAIGActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_aig);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callus_btn:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:1-877-867-3783"));
                startActivity(callIntent);
                break;
            case R.id.emailus_btn:
                //String feedback;
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"zhisheng.zhou@aig.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Topic: I have question about my claim.");
                //feedback = feedbackBox.getText().toString();
                //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedback);
                startActivity(Intent.createChooser(emailIntent, "Choose an App to send your Email"));
                break;
            case R.id.visitus_btn:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.aig.com"));
                startActivity(intent);
                break;
        }
    }
}
