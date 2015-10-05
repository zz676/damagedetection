package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aig.science.damagedetection.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhisheng Zhou
 */
public class NewClaimActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    private Button addPhotoBtn;
    private ImageView imageView;
    List<String> imagesPathList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_claim);
        imageView = (ImageView) findViewById(R.id.imageView);
        addPhotoBtn = (Button) findViewById(R.id.addPhoto_btn);
        addPhotoAddButtonListener();
    }

    public void addPhotoAddButtonListener() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewClaimActivity.this, "File a new claim!", Toast.LENGTH_SHORT).show();
                startActivityForResult(Intent.createChooser(intent, "Select Photos"),SELECT_PICTURE );
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = null;
                if(data!=null){
                    selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri);
                    imagesPathList = getPath(selectedImageUri);
                    for (String urlstr : imagesPathList) {
                        System.out.println("Image Path : " + urlstr + "\n");
                    }
                }
            }
        }
    }

    public List<String> getPath(Uri uri) {

        List<String> photosPathList = new ArrayList<String>();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor mCur = getContentResolver().query(uri, projection, null, null, null);
        int count = mCur.getCount();
        while(mCur.moveToNext()){
            int column_index = mCur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            photosPathList.add(mCur.getString(column_index));
           // System.out.println(mCur.getString(column_index));
        }
        mCur.close();

/*        mCur.moveToFirst();
        if (mCur != null && mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
                int column_index = mCur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                photosPathList.add(mCur.getString(column_index));
                mCur.moveToNext();
            }
        }*/
        return photosPathList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_claim, menu);
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
}
