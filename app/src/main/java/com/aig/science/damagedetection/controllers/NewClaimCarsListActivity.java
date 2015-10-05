package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.ImageAdaptor;
import com.aig.science.damagedetection.adaptor.PoliciesListViewAdapter;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.utilities.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Zhisheng Zhou
 */
public class NewClaimCarsListActivity extends Activity {

    protected static final String PASS_POLICY = "com.aig.science.damagedetection.CarsListActivity.POLICYOBJECT";
    private static final String SHOOTING_VIEW = "com.aig.science.damagedetection.CarsListActivity.shootview";
    private List<PolicyInfo> policiesList = new ArrayList<PolicyInfo>();
    private LinearLayout carlistNoPolicyLinearlayout;
    private TextView noPolicyTxtview;
    private TextView selectPolicyTxtview;
    private ListView carListview;
    private Button carButton;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private List<String> logInfo;
    private PoliciesListViewAdapter policyAdapter;
    private static final String TAG = "NewClaimActivity";
    private Context mContext;
    private Dialog selectViewsDialog;
    private ListView selecViewListView;
    private List<String> shootViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_list);
        carlistNoPolicyLinearlayout = (LinearLayout) findViewById(R.id.carlist_no_policy_linearlayout);
        noPolicyTxtview = (TextView) findViewById(R.id.carlist_no_policy_txtview);
        carListview = (ListView) findViewById(R.id.carlist_listview);
        selectPolicyTxtview = (TextView) findViewById(R.id.select_policy_txtview);
        dbHelper = new DatabaseHelper(NewClaimCarsListActivity.this);
        session = new SessionManager(this);
        logInfo = session.getUserInfo();
        policiesList = dbHelper.getUserPolicies(logInfo.get(0));
        fillListView();
        mContext = this;
    }

    private void fillListView() {
        if (policiesList.size() > 0) {
            carlistNoPolicyLinearlayout.setVisibility(View.GONE);
            policyAdapter = new PoliciesListViewAdapter(this, policiesList);
            Collections.sort(policiesList, new Comparator<PolicyInfo>() {
                @Override
                public int compare(PolicyInfo policyInfo1, PolicyInfo policyInfo2) {
                    return policyInfo1.getMake().compareTo(policyInfo2.getMake());
                }
            });
            carListview.setAdapter(policyAdapter);
            policyAdapter.notifyDataSetChanged();
            final Intent takePhotoIntent = new Intent(this, TakePhotosActivity.class);
            carListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                    try{
                        takePhotoIntent.putExtra(PASS_POLICY, policiesList.get(position));
                        selectViewsDialog = new Dialog(NewClaimCarsListActivity.this);
                        selectViewsDialog.getWindow();
                        //showCarPartsWindow.setTitle("All parts: ");
                        selectViewsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        selectViewsDialog.setContentView(R.layout.popup_select_shoot_views);
                        selectViewsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        Button closeCarPartsPopup = (Button) selectViewsDialog.findViewById(R.id.popup_shoot_views_closebtn);
                        closeCarPartsPopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectViewsDialog.dismiss();
                            }
                        });
                        selecViewListView = (ListView) selectViewsDialog.findViewById(R.id.shoot_views_listview);
                        shootViews = Arrays.asList(getResources().getStringArray(R.array.shoot_views));
                        ShootViewsAdapter shootViewsAdapter = new ShootViewsAdapter(NewClaimCarsListActivity.this, shootViews);
                        selecViewListView.setAdapter(shootViewsAdapter);
                        selecViewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (selectViewsDialog != null) {
                                    selectViewsDialog.dismiss();
                                }

                                switch (shootViews.get(position).toLowerCase()) {
                                    case "3d model":
                                        takePhotoIntent.putExtra(SHOOTING_VIEW, "3d Model");
                                        break;
                                    case "parts list":
                                        takePhotoIntent.putExtra(SHOOTING_VIEW, "Parts List");
                                        break;
                                    case "vehicle views":
                                        takePhotoIntent.putExtra(SHOOTING_VIEW, "Vehicle Views");
                                        break;
                                }
                                new LoadModelTask(mContext,takePhotoIntent).execute();
                            }
                        });
                        selectViewsDialog.show();
                    }catch(Exception ex){
                        Log.d(TAG, ex.getMessage());
                    }
                }
            });
        } else {
            selectPolicyTxtview.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_policy_button:
                Intent addPolicyIntent = new Intent(this, PolicyInfoActivity.class);
                addPolicyIntent.putExtra("ACTION","NEW");
                startActivity(addPolicyIntent);
                break;
        }
    }

    private void addCarButtons() {
        for (final PolicyInfo policyInfo : policiesList) {
            carButton = new Button(this);
            final String carButtonString = policyInfo.getMake() + " " + policyInfo.getModel() + " " + policyInfo.getColor();
            carButton = (Button) getLayoutInflater().inflate(R.layout.car_button_template, null);
            carButton.setText(carButtonString);
/*            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) carButton.getLayoutParams();
            params.bottomMargin= 20;
            carButton.setLayoutParams(params);*/
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 30);
            carButton.setLayoutParams(params);
            final Intent intent = new Intent(this, TakePhotosActivity.class);
            intent.putExtra("CAR_BUTTON_STRING", carButtonString);
            carButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(CarsListActivity.this, "Selected: " + carButtonString, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
            carlistNoPolicyLinearlayout.addView(carButton);
        }
    }

   @Override
    public void onBackPressed() {

        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(homeScreenIntent);
    }

    private class ShootViewsAdapter extends BaseAdapter {

        private Context context;
        private List<String> shootingViews;

        public ShootViewsAdapter(Context context, List<String> shootingViews) {

            this.context = context;
            this.shootingViews = shootingViews;
        }
        @Override
        public int getCount() {

            if (shootingViews != null) {
                return shootingViews.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (shootingViews != null) {
                try {
                    return shootingViews.get(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.popup_select_shoot_views_item, null);
                ViewHolder holder = new ViewHolder();

                String shootingViewName = shootingViews.get(position);
                holder.shootingViewName = (TextView) convertView
                        .findViewById(R.id.popup_shoot_views_item_txtview);
                holder.shootingViewName.setText(shootingViewName);

                holder.shootViewIcon = (ImageView) convertView.findViewById(R.id.popup_shoot_views_item_imageview);
                switch (shootingViewName.toLowerCase()) {
                    case "3d model":
                        //holder.shootViewIcon.setImageResource(getResources().getIdentifier("global", "drawable",  getPackageName()));
                        holder.shootViewIcon.setBackgroundResource(R.drawable.global);
                        break;
                    case "parts list":
                        //holder.shootViewIcon.setImageResource(getResources().getIdentifier("ic_action_view_as_list", "drawable",  getPackageName()));
                        holder.shootViewIcon.setBackgroundResource(R.drawable.list_view);
                        break;
                    case "vehicle views":
                        //holder.shootViewIcon.setImageResource(getResources().getIdentifier("whole_car", "drawable",  getPackageName()));
                        holder.shootViewIcon.setBackgroundResource(R.drawable.whole_car);
                        break;
                }
                convertView.setTag(holder);

                return convertView;
            }catch (Exception ex){
                Log.d(TAG, ex.getMessage());
            }
            return null;
        }
        @Override
        public boolean isEmpty() {
            return (shootingViews.size() == 0) ? true : false;
        }

        private class ViewHolder {
            ImageView shootViewIcon;
            TextView shootingViewName;
        }
    }

    private class LoadModelTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private Context context;
        private Intent takePhotoIntent;
        private LoadModelTask(Context context, Intent takePhotoIntent){
            this.context = context;
            this.takePhotoIntent = takePhotoIntent;
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
            Toast.makeText(mContext,"It will take a few seconds to load the 3d model. Please wait.",Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading 3d model...");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //android.os.Debug.waitForDebugger();
            try{
                if(takePhotoIntent!=null)
                {
                    startActivity(takePhotoIntent);
                }
            } catch (Exception ex){
                Log.d(TAG, ex.getMessage());
            }
            return null;
        }
    }
}
