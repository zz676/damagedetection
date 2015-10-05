package com.aig.science.damagedetection.adaptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.controllers.PolicyInfoActivity;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.PolicyInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PolicyListAdapter extends BaseAdapter {

    /**
     * The LayoutInflater.
     */
    private LayoutInflater mInflater;

    /**
     * The context.
     */
    Context context;

    /**
     * The holder.
     */
    ViewHolder holder;

    String isPolicyStatus;

    /**
     * The data ArrayList.
     */
    ArrayList<PolicyInfo> dataArrayList;

    public PolicyListAdapter(Context context,
                             ArrayList<PolicyInfo> policyInfoList, String isPolicyStatus) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.dataArrayList = policyInfoList;
        this.isPolicyStatus = isPolicyStatus;

        LayoutInflater layoutInflater = (LayoutInflater) ((Activity) context)
                .getBaseContext().getSystemService(
                        context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (dataArrayList != null) {
            return dataArrayList.size();

        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (dataArrayList != null) {
            try {
                return dataArrayList.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.custom_list_policy, null);
        holder = new ViewHolder();

        final PolicyInfo policyInfo = dataArrayList.get(position);

        final String make = policyInfo.getMake();
        final String model = policyInfo.getModel();
        final String color = policyInfo.getColor();

        final String userID = policyInfo.getUserId();
        final String policyID = policyInfo.getPolicyId();

        holder.carInfo = (TextView) convertView
                .findViewById(R.id.list_policy_detail_car_info);
        holder.carInfo.setText(make + " " + model + ", " + color);
        convertView.setTag(holder);
        return convertView;
    }

    public void showProgress() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        long delayInMillis = 1000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        }, delayInMillis);
    }

    /**
     * The Class ViewHolder.
     */
    static class ViewHolder {
        TextView carInfo;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
