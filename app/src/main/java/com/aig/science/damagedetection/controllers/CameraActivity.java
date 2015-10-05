package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aig.science.damagedetection.FromXML;
import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.logger.Log;
import com.aig.science.damagedetection.ui.widget.CameraPreview;

import java.io.File;
import java.lang.reflect.Method;

import static com.aig.science.damagedetection.helper.CameraHelper.cameraAvailable;
import static com.aig.science.damagedetection.helper.CameraHelper.getCameraInstance;
import static com.aig.science.damagedetection.helper.MediaHelper.getOutputMediaFile;
import static com.aig.science.damagedetection.helper.MediaHelper.saveToFile;

/**
 * Takes a photo saves it to the SD card and returns the path of this photo to the calling Activity
 *
 * @author Zhisheng Zhou
 */
public class CameraActivity extends Activity implements PictureCallback {

    protected final static String PART_NAME_STRING = "com.aig.science.damagedetection.controllers.part_name_string";
    private final static String TAG = "CameraActivity";
    private static boolean isLandLayout = false;// the status of orientation of camera screen
    protected static final String IMAGE_PATH = "com.aig.science.damagedetection.controllers.CameraActivity.IMAGE_PATH";

    private Camera camera;
    private CameraPreview cameraPreview;
    private ImageView overlayImageView;
    private static int angle;
    private static String carPartName;
    private ViewGroup.LayoutParams layoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        angle = getIntent().getIntExtra(TakePhotosActivity.ANGLE_STRING, 0);
        carPartName = getIntent().getStringExtra(PART_NAME_STRING);
        setContentView(R.layout.activity_camera);
        setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not available at all
        overlayImageView = (ImageView) findViewById(R.id.overlay_imageview);
        if (carPartName != null) {
            setOverlayImageViewForCarPart();
        } else {
            setOverlayImageView();
        }

        camera = getCameraInstance();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
        if (cameraAvailable(camera)) {
            initCameraPreview();
        } else {
            finish();
        }
    }

    // Show the camera view on the activity
    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.setFocusable(true);
        cameraPreview.setFocusableInTouchMode(true);
        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("down", "focusing now");
                    camera.autoFocus(null);
                }

                return false;
            }
        });

        if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            camera.setDisplayOrientation(0);
        } else {
            camera.setDisplayOrientation(90);
        }
        //camera.setDisplayOrientation(90);
        cameraPreview.init(camera);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged is called!");
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setContentView(R.layout.activity_camera_lan);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            camera.setDisplayOrientation(0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //setContentView(R.layout.activity_camera);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            camera.setDisplayOrientation(90);
        }
    }

    @FromXML
    public void onCaptureClick(View button) {
        // Take a picture with a callback when the photo has been created
        // Here you can add callbacks if you want to give feedback when the picture is being taken
        camera.takePicture(null, null, this);
    }

    public void onRotateClick(View button) {
        // Take a picture with a callback when the photo has been created
        // Here you can add callbacks if you want to give feedback when the picture is being taken
        if (isLandLayout) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            camera.setDisplayOrientation(90);
            isLandLayout = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            camera.setDisplayOrientation(0);
            isLandLayout = true;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "Picture taken");
        String path = savePictureToFileSystem(data);
        setResult(path);
        finish();
    }

    private static String savePictureToFileSystem(byte[] data) {
        File file = getOutputMediaFile();
        saveToFile(data, file);
        return file.getAbsolutePath();
    }

    private void setResult(String path) {
        Intent intent = new Intent();
        intent.putExtra(IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Always remember to release the camera when you are finished
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    protected void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[]{angle});
        } catch (Exception e1) {
        }
    }

    private void setOverlayImageViewForCarPart() {

        String matchedOutlineName = matchPartNameAndOutline(carPartName);
        int resId = this.getResources().getIdentifier(matchedOutlineName, "drawable", this.getPackageName());
        if (resId == 0) {
            //overlayImageView.setImageResource(R.drawable.default_outline);
        } else {
            overlayImageView.setImageResource(resId);
        }
    }

    /**
     * select the right outline for a part
     *
     * @param carPartName
     * @return
     */
    public String matchPartNameAndOutline(String carPartName) {
        String matchedOutlineName = "";
        if (carPartName.toLowerCase().contains("rim") || carPartName.toLowerCase().contains("tire"))
            carPartName = "tire";

        switch (carPartName.toLowerCase()) {

            case "passenger side mirror":
                matchedOutlineName = "passenger_side_mirror";
                break;
            case "driver side mirror":
                matchedOutlineName = "driver_side_mirror";
                break;


            case "front left headlight":
                matchedOutlineName = "front_left_headlight";
                break;
            case "front right headlight":
                matchedOutlineName = "front_right_headlight";
                break;
            case "right back light":
                matchedOutlineName = "right_back_light";
                break;
            case "left back light":
                matchedOutlineName = "left_back_light";
                break;


            case "back driver side wheel":
                matchedOutlineName = "wheel";
                break;
            case "back passenger side wheel":
                matchedOutlineName = "wheel";
                break;
            case "front driver side wheel":
                matchedOutlineName = "wheel";
                break;
            case "front passenger side wheel":
                matchedOutlineName = "wheel";
                break;

            case "back bumper":
                matchedOutlineName = "back_bumper";
                break;
            case "front bumper":
                matchedOutlineName = "front_bumper";
                break;


            case "left side protector":
                matchedOutlineName = "left_side_protector";
                break;
            case "right side protector":
                matchedOutlineName = "right_side_protector";
                break;
            case "back body part":
                matchedOutlineName = "back_body_part";
                break;
            case "top":
                matchedOutlineName = "top";
                break;
            case "right side body part":
                matchedOutlineName = "right_side_body_part";
                break;
            case "left side body part":
                matchedOutlineName = "left_side_body_part";
                break;
            case "front right wing":
                matchedOutlineName = "front_right_wing";
                break;
            case "front left wing":
                matchedOutlineName = "front_left_wing";
                break;


            case "trunk cover":
                matchedOutlineName = "trunk_cover";
                break;


            case "windshield":
                matchedOutlineName = "windshield";
                break;
            case "back window":
                matchedOutlineName = "back_window";
                break;
            case "right window":
                matchedOutlineName = "right_window";
                break;
            case "left window":
                matchedOutlineName = "left_window";
                break;


            case "front logo sign":
                matchedOutlineName = "front_logo_sign";
                break;
            case "back logo sign":
                matchedOutlineName = "back_logo_sign";
                break;
            case "right exhaust pipe":
                matchedOutlineName = "right_exhaust_part";
                break;
            case "left exhaust pipe":
                matchedOutlineName = "left_exhaust_part";
                break;


            case "front driver side door":
                matchedOutlineName = "front_driver_side_door";
                break;
            case "front passenger side door":
                matchedOutlineName = "front_passenger_side_door";
                break;
            case "hood":
                matchedOutlineName = "hood";
                break;


            case "front grill":
                matchedOutlineName = "front_grill";
                break;
        }
        return matchedOutlineName;
    }


    /**
     * set the right outline on the camera for each angle
     */
    private void setOverlayImageView() {
        switch (angle) {
            case 1:
                //"front"
                overlayImageView.setImageResource(R.drawable.tran_front);
                break;
            case 2:
                //"front_left"
                overlayImageView.setImageResource(R.drawable.tran_left_front);
                break;
            case 3:
                //"front_right"
                overlayImageView.setImageResource(R.drawable.tran_right_front);
                break;
            case 4:
                //"left"
                overlayImageView.setImageResource(R.drawable.tran_left);
                break;
            case 5:
                //"right"
                overlayImageView.setImageResource(R.drawable.tran_right);
                break;
            case 6:
                //"back"
                overlayImageView.setImageResource(R.drawable.tran_back);
                break;
            case 7:
                // "back_left"
                overlayImageView.setImageResource(R.drawable.tran_left_back);
                break;
            case 8:
                //"back_right"
                overlayImageView.setImageResource(R.drawable.tran_right_back);
                break;
        }
    }
}
