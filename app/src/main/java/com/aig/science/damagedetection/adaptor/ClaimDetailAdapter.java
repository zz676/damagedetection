package com.aig.science.damagedetection.adaptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.controllers.PolicyInfoActivity;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.Claim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by poopatil on 11/10/2014.
 */
public class ClaimDetailAdapter extends BaseAdapter {

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
    private ArrayList<Claim> dataArrayList;

    public ClaimDetailAdapter(Context context,
                              ArrayList<Claim> claimsInfoList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.dataArrayList = claimsInfoList;

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
        convertView = mInflater.inflate(R.layout.custom_list_claims, null);
        holder = new ViewHolder();

        final Claim claims = dataArrayList.get(position);

        final String userID = claims.getUserId();
        final String policyID = claims.getPolicyId();
        final String policyNum = claims.getPolicyNumber();
        final String make = claims.getMake();
        final String model = claims.getModel();
        final String color = claims.getColor();
        final String time = claims.getSubmittedTime();

        holder.carInfo = (TextView) convertView
                .findViewById(R.id.list_claim_detail_car_info);
        holder.policyID = (TextView) convertView
                .findViewById(R.id.list_claim_detail_policyID);
        holder.date = (TextView) convertView
                .findViewById(R.id.list_claim_detail_dateID);
        holder.progressBtn = (ImageView) convertView
                .findViewById(R.id.list_claim_detail_progressbtnID);
        if (claims.isCompleted()) {
            holder.progressBtn.setBackgroundResource(R.drawable.complete);
        } else {
            holder.progressBtn.setBackgroundResource(R.drawable.progresss);
        }
        holder.carInfo.setText(make + " " + model + ", " + color);
        holder.policyID.setText(policyNum);
        holder.date.setText(time);
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

        TextView carInfo, policyID, date;
        ImageView progressBtn, editBtn;
    }
}
