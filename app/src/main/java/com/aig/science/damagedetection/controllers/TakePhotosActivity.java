package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.CarPartsListAdapter;
import com.aig.science.damagedetection.adaptor.TwoWayListAdapter;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.models.Image;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.services.ServiceHandler;
import com.aig.science.damagedetection.utilities.GPSTracker;
import com.aig.science.damagedetection.utilities.ImageResizer;
import com.aig.science.dd3d.DD3DGLSurfaceView;
import com.aig.science.dd3d.DD3DRenderer;
import com.aig.science.dd3d.PartObject;

import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Zhisheng Zhou
 */
public class TakePhotosActivity extends Activity {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public final static String TAG = "TAKEPHOTOSACTIVITY";
    protected final static String ANGLE_STRING = "com.aig.science.damagedetection.controllers.angle_string";
    private final static String CAR_BUTTON_STRING = "CAR_BUTTON_STRING";
    private static final String EXTRA_TAG = "com.aig.science.damagedetection.CarsListActivity.POLICYOBJECT";
    protected final static String PART_NAME_STRING = "com.aig.science.damagedetection.controllers.part_name_string";
    private static final String SHOOTING_VIEW = "com.aig.science.damagedetection.CarsListActivity.shootview";
    //private Dialog add_another_photo_dialog;
    private static final int ACTION_TAKE_PHOTO = 0;
    //private GridView extraGridView;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static boolean isTakeRegularPhotos = true;
    private Dialog showCarPartsListWindow;
    private boolean is3Dview = true;
    private MenuItem asListMenuItem;
    private MenuItem switchViewMenuItem;
    private boolean isTakePhotoForPart = false;
    private Button.OnClickListener mTakePhotoForPartClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhotoForPartdialog.dismiss();
            isTakeRegularPhotos = true;
            isTakePhotoForPart = true;
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
        }
    };
    ;
    ImageButton.OnClickListener mBtnShootPhotoClickListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View v) {
            isTakeRegularPhotos = false;
            isTakePhotoForPart = false;
            addMorePhotos_dialog.dismiss();
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
        }
    };
    private static int photo_take_status = 0;
    public static int numOfPixels;
    private static String photosFolder = "";
    private static int angle;
    private Dialog add_another_photo_dialog;
    private Dialog addMorePhotos_dialog;
    private String carButtonString;
    private TextView photoTakeTitleTxtView;
    private ImageButton frontBtn;
    private ImageButton backBtn;
    private ImageButton frontRightBtn;
    private ImageButton frontLeftBtn;
    private ImageButton backRightBtn;
    private ImageButton backLeftBtn;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private Button submitBtn;
    private ImageButton currentBtn;
    private ViewSwitcher photoTakeViewSwitcher;
    private Animation slide_in_left, slide_out_right;

    private ImageButton.OnClickListener mImageBtnShootPhotoClickListener;
    private Button cancelBtn;
    private TwoWayView mListView;
    private Toast mToast;
    private String mClickMessage;
    private String mScrollMessage;
    private String mStateMessage;
    private String mCurrentPhotoPath;
    private Bitmap bitMap;
    private Uri contentUri;
    private GPSTracker gps;
    private double longitude;
    private double latitude;
    private View selectedListItem = null;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private List<Image> imagesList = new ArrayList<Image>();
    private List<String> extraImagesPathList = new ArrayList<String>();
    private PolicyInfo policyInfo;
    private DatabaseHelper dbHelper;
    private ServiceHandler handle;

    private DD3DGLSurfaceView mGLSurfaceView;
    private DD3DRenderer mRenderer;
    private ConfigurationInfo configurationInfo;
    private DisplayMetrics displayMetrics;

    private ProgressDialog loadingObjDialog;
    private List<PartObject> carparts = new ArrayList<>();
    private ExpandableListView carPartsListView;
    private CarPartsListAdapter carPartsAdapter;
    private int lastExpandedPosition = -1;
    private List<String> listDataHeader;
    private HashMap<String, List<PartObject>> listDataChild;
    private PartObject currentPart;
    private Dialog takePhotoForPartdialog;
    private final boolean isFirstLoading = true;
    private Dialog cancelClaimDialog;
    private String selectedViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photos);

        if (getIntent() != null) {
            policyInfo = getIntent().getParcelableExtra(NewClaimCarsListActivity.PASS_POLICY);
        }
        dbHelper = new DatabaseHelper(this);
        carButtonString = (String) getIntent().getExtras().get(CAR_BUTTON_STRING);
        photoTakeTitleTxtView = (TextView) findViewById(R.id.photo_take_title_txtview);
        //photoTakeTitleTxtView.setText(getResources().getString(R.string.photo_take_title) + carButtonString + getResources().getString(R.string.photo_take_title_angles));
        submitBtn = (Button) findViewById(R.id.submit_photos_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmitPhotos();
            }
        });

        cancelBtn = (Button) findViewById(R.id.cancel_submit_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCancelCalim();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        initViews();
    }

    private void initViews(){

        photoTakeViewSwitcher = (ViewSwitcher) findViewById(R.id.phototake__viewswitcher);
        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        photoTakeViewSwitcher.setInAnimation(slide_in_left);
        photoTakeViewSwitcher.setOutAnimation(slide_out_right);
        selectedViewName = getIntent().getStringExtra(SHOOTING_VIEW);
        initVehicleView();
        init3DView();

        switch (selectedViewName.toLowerCase()) {
            case "3d model":
                is3Dview = true;
                break;
            case "parts list":
                handleCarPartsPopUpWindow();
                break;
            case "vehicle views":
                photoTakeViewSwitcher.showPrevious();
                is3Dview = false;
                break;
        }
    }

    private void init3DView(){

        Toast.makeText(this, "Before loading opengl: " + new Date().toString(),Toast.LENGTH_SHORT).show();
/*        loadingObjDialog = new ProgressDialog(this);
        loadingObjDialog.setMessage("Loading 3D car model ...");
        loadingObjDialog.setCancelable(false);
        loadingObjDialog.isIndeterminate();
        loadingObjDialog.show();*/
        mGLSurfaceView = (DD3DGLSurfaceView) findViewById(R.id.phototake_gl_surface_view);
        loadOpenGL();
    }

    private void initVehicleView(){
        mImageBtnShootPhotoClickListener = new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.front_angle_btn:
                        //"front"
                        angle = 1;
                        break;
                    case R.id.front_left_angle_btn:
                        //"front_left"
                        angle = 2;
                        break;
                    case R.id.front_right_angle_btn:
                        // "front_right"
                        angle = 3;
                        break;
                    case R.id.left_angle_btn:
                        //"left"
                        angle = 4;
                        break;
                    case R.id.right_angle_btn:
                        //"right"
                        angle = 5;
                        break;
                    case R.id.back_angle_btn:
                        //"back"
                        angle = 6;
                        break;
                    case R.id.back_left_angle_btn:
                        //"back_left"
                        angle = 7;
                        break;
                    case R.id.back_right_angle_btn:
                        // "back_right"
                        angle = 8;
                        break;
                }
                isTakeRegularPhotos = true;
                isTakePhotoForPart = false;
                currentBtn = (ImageButton) findViewById(v.getId());
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
            }
        };

        frontBtn = (ImageButton) findViewById(R.id.front_angle_btn);
        setImageBtnListenerOrDisable(
                frontBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        backBtn = (ImageButton) findViewById(R.id.back_angle_btn);
        setImageBtnListenerOrDisable(
                backBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        frontRightBtn = (ImageButton) findViewById(R.id.front_right_angle_btn);
        setImageBtnListenerOrDisable(
                frontRightBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        frontLeftBtn = (ImageButton) findViewById(R.id.front_left_angle_btn);
        setImageBtnListenerOrDisable(
                frontLeftBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        backRightBtn = (ImageButton) findViewById(R.id.back_right_angle_btn);
        setImageBtnListenerOrDisable(
                backRightBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        backLeftBtn = (ImageButton) findViewById(R.id.back_left_angle_btn);
        setImageBtnListenerOrDisable(
                backLeftBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        leftBtn = (ImageButton) findViewById(R.id.left_angle_btn);
        setImageBtnListenerOrDisable(
                leftBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        rightBtn = (ImageButton) findViewById(R.id.right_angle_btn);
        setImageBtnListenerOrDisable(
                rightBtn,
                mImageBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        mClickMessage = "";
        mScrollMessage = "";
        mStateMessage = "";
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        mListView = (TwoWayView) findViewById(R.id.twoway_list);
        mListView.setItemMargin(10);
        mListView.setLongClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View child, int position,
                                    long id) {
            }
        });
        numOfPixels = (int) ImageResizer.convertDpToPixel(250, this);
    }


    private void loadOpenGL() {
        try{
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
            if (supportsEs2) {
                // Request an OpenGL ES 2.0 compatible context.
                mGLSurfaceView.setEGLContextClientVersion(2);
                displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                // Set the renderer to our demo renderer, defined below.
                mRenderer = new DD3DRenderer(this);
                mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);
            } else {
                // This is where you could create an OpenGL ES 1.x compatible
                // renderer if you wanted to support both ES 1 and ES 2.
            }
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }


    private void refreshToast() {
        StringBuffer buffer = new StringBuffer();

        if (!TextUtils.isEmpty(mClickMessage)) {
            buffer.append(mClickMessage);
        }

        if (!TextUtils.isEmpty(mScrollMessage)) {
            if (buffer.length() != 0) {
                buffer.append("\n");
            }

            buffer.append(mScrollMessage);
        }

        if (!TextUtils.isEmpty(mStateMessage)) {
            if (buffer.length() != 0) {
                buffer.append("\n");
            }

            buffer.append(mStateMessage);
        }

        mToast.setText(buffer.toString());
        mToast.show();
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    private void setImageBtnListenerOrDisable(
            ImageButton btn,
            ImageButton.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            Toast toast = Toast.makeText(this, "Can't take photo.", Toast.LENGTH_SHORT);
            toast.show();
            btn.setClickable(false);
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent;
        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        if (isTakeRegularPhotos) {
            if(isTakePhotoForPart){
                takePictureIntent = new Intent(this, CameraActivity.class);
                takePictureIntent.putExtra(PART_NAME_STRING, currentPart.getPartName());
            } else {
                takePictureIntent = new Intent(this, CameraActivity.class);
                takePictureIntent.putExtra(ANGLE_STRING, angle);
            }
        } else {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            photosFolder = storageDir.getAbsolutePath();
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Image image;
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {

                    if (isTakeRegularPhotos) {
                        image = new Image();
                        mCurrentPhotoPath = data.getStringExtra(CameraActivity.IMAGE_PATH);
                        image.setImageURI(mCurrentPhotoPath);
                        if(isTakePhotoForPart){
                            image.setImageType(currentPart.getPartName());
                            for(PartObject part : carparts){
                                if(part.getPartName().equals(currentPart.getPartName())){
                                    part.setIsPhotoTaken(true);
                                    part.setPartPhotoUrl(mCurrentPhotoPath);
                                }
                            }
                            if(carPartsAdapter != null){
                                prepareListData();
                                carPartsAdapter = new CarPartsListAdapter(this,listDataHeader,listDataChild);
                                carPartsListView.setAdapter(carPartsAdapter);
                                carPartsListView.expandGroup(lastExpandedPosition);
                                //carPartsAdapter.notifyDataSetChanged();
                                //carPartsListView.invalidate();
                            }
                        } else{
                            image.setImageType(String.valueOf(angle));
                            currentBtn.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(mCurrentPhotoPath, (int) (numOfPixels * 1.16777), numOfPixels));
                            currentBtn.setBackgroundColor(Color.TRANSPARENT);
                        }
                        imagesList.add(image);
                    } else {
                        image = new Image();
                        galleryAddPic();
                        if (add_another_photo_dialog != null) {
                            add_another_photo_dialog.dismiss();
                        }
                        //isTakeRegularPhotos = true;
                        image.setImageURI(mCurrentPhotoPath);
                        image.setImageType("EXTRA");
                        imagesList.add(image);
                        extraImagesPathList.add(mCurrentPhotoPath);
                        //extraGridView.setAdapter(new ExtraGalleryAdapter(this, extraImagesPathList));
                        mListView.setAdapter(new TwoWayListAdapter(TakePhotosActivity.this, extraImagesPathList));
                        mListView.setSelection(extraImagesPathList.size() - 1);
                        handAotherPhotoPopUpWindow();
                    }
                }
                break;
            }
        }
    }

    public void handAotherPhotoPopUpWindow() {
        //Show "Add another photo popup window"
        add_another_photo_dialog = new Dialog(this);
        add_another_photo_dialog.getWindow();
        // dialog.setTitle("Please take a photo.");
        add_another_photo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add_another_photo_dialog.setContentView(R.layout.popup_window_add_another);

        Button yesAnotherBtn = (Button) add_another_photo_dialog.findViewById(R.id.yes_another_btn);
        Button noAnotherBtn = (Button) add_another_photo_dialog.findViewById(R.id.no_another_btn);
        setBtnListenerOrDisable(
                yesAnotherBtn,
                mBtnShootPhotoClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        //homeScreenIntent.putExtra("Claim",);
        noAnotherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                add_another_photo_dialog.dismiss();
            }
        });
        //add_another_photo_dialog.setCancelable(false);
        add_another_photo_dialog.setCancelable(false);
        add_another_photo_dialog.show();
    }

    public Drawable fromUriToDrawable(Uri uri) {
        Drawable drawable = null;
        try {
            //URL url = new URL(path);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            drawable = Drawable.createFromStream(inputStream, uri.toString());

        } catch (FileNotFoundException ex) {
            Log.v(TAG, ex.getMessage());
            drawable = getResources().getDrawable(R.drawable.default_image);
        }
        return drawable;
    }

    public void handleSubmitPhotos() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow();
        // dialog.setTitle("Please take a photo.");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_window_take_one);

        // TextView popupTxt = (TextView)findViewById(R.id.popup_txtview);
        //popupTxt.setText("Please take a photo");

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
        Button continueBtn = (Button) dialog.findViewById(R.id.continue_btn);

        final Intent addMorePhotoActivity = new Intent(this, AddMorePhotosActivity.class);
        final Intent HOMESCREEN_INTENT = new Intent(this, HomeScreenActivity.class);
        //homeScreenIntent.putExtra("Claim",);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                handleCancelCalim();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        if (imagesList.size() < 1) {
            dialog.show();
        } else {
            addMorePhotos_dialog = new Dialog(this);
            addMorePhotos_dialog.getWindow();
            // dialog.setTitle("Please take a photo.");
            addMorePhotos_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addMorePhotos_dialog.setContentView(R.layout.popup_window_addmore);

            Button yesBtn = (Button) addMorePhotos_dialog.findViewById(R.id.yes_btn);
            Button noBtn = (Button) addMorePhotos_dialog.findViewById(R.id.no_btn);
            setBtnListenerOrDisable(
                    yesBtn,
                    mBtnShootPhotoClickListener,
                    MediaStore.ACTION_IMAGE_CAPTURE
            );

            //homeScreenIntent.putExtra("Claim",);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    addMorePhotos_dialog.dismiss();
                    handleGetLocationPopupWindow();
                    //getCurrentLocation();
                }
            });
            addMorePhotos_dialog.setCancelable(false);
            addMorePhotos_dialog.show();
        }
    }

    private void handleGetLocationPopupWindow() {
        final Dialog getLocationDialog = new Dialog(this);
        getLocationDialog.getWindow();
        // dialog.setTitle("Please take a photo.");
        getLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getLocationDialog.setContentView(R.layout.location_popup_window);

        Button locationYesBtn = (Button) getLocationDialog.findViewById(R.id.yes_location_btn);
        Button locationNoBtn = (Button) getLocationDialog.findViewById(R.id.no_location_btn);
        //homeScreenIntent.putExtra("Claim",);
        locationYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getLocationDialog.dismiss();
                getCurrentLocation();
                storeClaimAndImages();
                //new StoreClaimTask(TakePhotosActivity.this).execute();
            }
        });
        locationNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getLocationDialog.dismiss();
                storeClaimAndImages();
                //new StoreClaimTask(TakePhotosActivity.this).execute();
            }
        });
        getLocationDialog.setCancelable(false);
        getLocationDialog.show();
    }

    @Override
    public void onBackPressed() {
            handleCancelCalim();
    }


    private void handleCancelCalim(){
        cancelClaimDialog = new Dialog(this);
        cancelClaimDialog.getWindow();
        // dialog.setTitle("Please take a photo.");
        cancelClaimDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelClaimDialog.setContentView(R.layout.popup_cancel_claim);

        Button locationYesBtn = (Button) cancelClaimDialog.findViewById(R.id.yes_cancel_claim_btn);
        Button locationNoBtn = (Button) cancelClaimDialog.findViewById(R.id.no_cancel_claim_btn);
        final Intent HOMESCREEN_INTENT = new Intent(this, HomeScreenActivity.class);
        locationYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancelClaimDialog.dismiss();
                startActivity(HOMESCREEN_INTENT);
            }
        });
        locationNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancelClaimDialog.dismiss();
            }
        });
        cancelClaimDialog.setCancelable(false);
        cancelClaimDialog.show();
    }


    /**
     * get the longitude and latitude of user when filing the claim
     *
     * @return
     */
    public boolean getCurrentLocation() {
        gps = new GPSTracker(TakePhotosActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
            return false;
        }
    }

    /**
     * insert claim and images
     *
     * @return
     */
    private boolean storeClaimAndImages() {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
            String claimID = UUID.randomUUID().toString();
            Claim claim = new Claim();
            claim.setUserId(policyInfo.getUserId());
            claim.setPolicyId(policyInfo.getPolicyId());
            claim.setClaimId(claimID);
            claim.setStatus("Submitted");
            claim.setLatitude(latitude);
            claim.setLongitude(longitude);
            claim.setSubmittedTime(df.format(new Date()));
            handle = new ServiceHandler(this);
            final JSONObject claimInfoJSON = claim.toInsertJSON(claim);
            if (claimInfoJSON != null) {
                handle.startServices(TAG, null, claimInfoJSON.toString());
            }
            Iterator<Image> itr = imagesList.iterator();
            while (itr.hasNext()) {
                Image image = itr.next();
                image.setClaimId(claimID);
                image.setImageId(UUID.randomUUID().toString());
                final JSONObject imageJSON = image.toInsertJSON(image);
                if (imageJSON != null) {
                    handle.startServices(TAG, null, imageJSON.toString());
                }
            }
            showCompeltedMsg();
            return false;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return true;
        }
    }

    private void showCompeltedMsg() {
        final Dialog showCompeltedMsgDialog = new Dialog(this);
        showCompeltedMsgDialog.getWindow();
        showCompeltedMsgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showCompeltedMsgDialog.setContentView(R.layout.popup_window_complete);
        final Intent HOMESCREEN_INTENT = new Intent(this, HomeScreenActivity.class);
        Button okBtn = (Button) showCompeltedMsgDialog.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showCompeltedMsgDialog.dismiss();
                startActivity(HOMESCREEN_INTENT);
            }
        });
        showCompeltedMsgDialog.setCancelable(false);
        showCompeltedMsgDialog.show();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.take_photos, menu);
        asListMenuItem = menu.findItem(R.id.phototake_action_carparts_aslist);
        switchViewMenuItem = menu.findItem(R.id.switch_takephoto_view_btn);
        if(is3Dview){
            switchViewMenuItem.setIcon(R.drawable.whole_car);
            switchViewMenuItem.setTitle(R.string.menu_phototake_swithview_wholecar);
        } else {
            switchViewMenuItem.setIcon(R.drawable.global);
            switchViewMenuItem.setTitle(R.string.menu_phototake_swithview_globalcar);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.switch_takephoto_view_btn:
                if (is3Dview) {
                    photoTakeViewSwitcher.showPrevious();
                    item.setIcon(R.drawable.global);
                    switchViewMenuItem.setTitle(R.string.menu_phototake_swithview_globalcar);
                    //asListMenuItem.setVisible(false);
                    is3Dview = false;
                } else {
                    photoTakeViewSwitcher.showPrevious();
                    item.setIcon(R.drawable.whole_car);
                    switchViewMenuItem.setTitle(R.string.menu_phototake_swithview_wholecar);
                    //asListMenuItem.setVisible(true);
                    is3Dview = true;
                }
                break;
            case R.id.phototake_action_carparts_aslist:
                handleCarPartsPopUpWindow();
                break;
            default:
                break;
        }
        return true;
    }

    public void handleCarPartsPopUpWindow() {

        if (showCarPartsListWindow != null) {
            showCarPartsListWindow.show();
        } else {
            showCarPartsListWindow = new Dialog(this);
            showCarPartsListWindow.getWindow();
            //showCarPartsWindow.setTitle("All parts: ");
            showCarPartsListWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showCarPartsListWindow.setContentView(R.layout.popup_carparts_list);
            showCarPartsListWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            Button closeCarPartsPopup = (Button) showCarPartsListWindow.findViewById(R.id.close_carparts_popup);
            closeCarPartsPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCarPartsListWindow.dismiss();
                }
            });
            carPartsListView = (ExpandableListView) showCarPartsListWindow.findViewById(R.id.parts_listview);
            carparts = mRenderer.getModel().getAllCarParts();
            prepareListData();

            if (carparts.size() > 0) {

                for(PartObject part : carparts){
                    if(currentPart != null) {
                        if (part.getPartName().equals(currentPart.getPartName()))
                            part.setIsPhotoTaken(true);
                        part.setPartPhotoUrl(mCurrentPhotoPath);
                    }
                }

                carPartsAdapter = new CarPartsListAdapter(this, listDataHeader,listDataChild);
                carPartsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        currentPart = (listDataChild.get(listDataHeader.get(groupPosition))).get(childPosition);
                        handlePartSelectedClick();
                        //Toast.makeText(getApplication(),currentPart.getPartName(),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                // Listview Group expanded listener
                carPartsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (lastExpandedPosition != -1
                                && groupPosition != lastExpandedPosition) {
                            carPartsListView.collapseGroup(lastExpandedPosition);
                        }
                        lastExpandedPosition = groupPosition;
                    }
                });
                carPartsListView.setAdapter(carPartsAdapter);
            }
            showCarPartsListWindow.setCancelable(false);
            showCarPartsListWindow.show();
        }
    }

    private void prepareListData(){
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        List<PartObject> frontParts = new ArrayList<>();
        List<PartObject> driverSideParts = new ArrayList<>();
        List<PartObject> passengerSideParts = new ArrayList<>();
        List<PartObject> backParts = new ArrayList<>();
        List<PartObject> otherParts = new ArrayList<>();

        listDataHeader.add("Front");
        listDataHeader.add("Driver Side");
        listDataHeader.add("Passenger Side");
        listDataHeader.add("Back");
        listDataHeader.add("Other Parts");

        for(PartObject part : carparts){

            switch (part.getPartName().toLowerCase()){

                case "windshield":
                case "hood":
                case "front left headlight":
                case "front right headlight":
                case "front grill":
                case "front bumper":
                case "front logo sign":
                    frontParts.add(part);
                    break;

                case "front driver side wheel":
                case "back driver side wheel":
                case "front left wing":
                case "front driver side door":
                case "left side protector":
                case "left side body part":
                case "driver side mirror":
                case "left window":
                    driverSideParts.add(part);
                    break;

                case "front passenger side wheel":
                case "back passenger side wheel":
                case "front right wing":
                case "front passenger side door":
                case "right side protector":
                case "right side body part":
                case "passenger side mirror":
                case "right window":
                    passengerSideParts.add(part);
                    break;

                case "back window":
                case "back bumper":
                case "left back light":
                case "right back light":
                case "trunk cover":
                case "back body part":
                case "back logo sign":
                case "left exhaust pipe":
                case "right exhaust pipe":
                    backParts.add(part);
                    break;

                case "top":
                    otherParts.add(part);
                    break;
            }
        }

        listDataChild.put(listDataHeader.get(0),frontParts);
        listDataChild.put(listDataHeader.get(1),driverSideParts);
        listDataChild.put(listDataHeader.get(2),passengerSideParts);
        listDataChild.put(listDataHeader.get(3),backParts);
        listDataChild.put(listDataHeader.get(4),otherParts);
    }


    public void handlePartSelectedClick() {
        takePhotoForPartdialog = new Dialog(this);
        takePhotoForPartdialog.getWindow();
        takePhotoForPartdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        takePhotoForPartdialog.setContentView(R.layout.popup_window_takephotoforpart);

        TextView popupTxt = (TextView) takePhotoForPartdialog.findViewById(R.id.takephotoforpart_txtview);
        popupTxt.setText("Do you want to take a photo for " + currentPart.getPartName());

        Button cancelBtn = (Button) takePhotoForPartdialog.findViewById(R.id.cancel_takephotoforpart_btn);
        Button yesBtn = (Button) takePhotoForPartdialog.findViewById(R.id.yes_takephotoforpart_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                takePhotoForPartdialog.dismiss();
            }
        });

        setBtnListenerOrDisable(
                yesBtn,
                mTakePhotoForPartClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        takePhotoForPartdialog.setCancelable(false);
        takePhotoForPartdialog.show();
    }

    // method to check if you have a Camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    // method to check you have Camera Apps
    private boolean hasDefualtCameraApp(String action) {
        final PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public ProgressDialog getLoadingObjDialog() {
        return loadingObjDialog;
    }

    public void setLoadingObjDialog(ProgressDialog loadingObjDialog) {
        this.loadingObjDialog = loadingObjDialog;
    }

    public boolean isFirstLoading() {
        return isFirstLoading;
    }

    public void setCurrentPart(PartObject currentPart) {
        this.currentPart = currentPart;
    }

    private class StoreClaimTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog dialog;
        private Context context;
        public StoreClaimTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(Boolean isFailed) {
            super.onPostExecute(isFailed);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (isFailed) {
                Toast.makeText(getApplicationContext(), "Failed to send your claim.",
                        Toast.LENGTH_SHORT).show();
            } else {
                showCompeltedMsg();
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
        protected Boolean doInBackground(Void... arg0) {
            //android.os.Debug.waitForDebugger();
            return storeClaimAndImages();
        }
    }
}
