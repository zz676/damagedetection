package com.aig.science.damagedetection.adaptor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.models.Place;
import com.aig.science.damagedetection.models.PolicyInfo;
import com.aig.science.damagedetection.utilities.ItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoliciesListViewAdapter extends BaseAdapter {

    private final String TAG = "PoliciesListViewAdapter";

    private Context mContext;
    private LayoutInflater infalter;
    private List<PolicyInfo> policiesList = new ArrayList<PolicyInfo>();

    public PoliciesListViewAdapter(Context context, List<PolicyInfo> policiesList) {
        infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.policiesList = policiesList;
    }

    @Override
    public int getCount() {
        return policiesList.size();
    }

    @Override
    public PolicyInfo getItem(int position) {
        return policiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addAll(ArrayList<PolicyInfo> policies) {

        try {
            this.policiesList.clear();
            this.policiesList.addAll(policies);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PolicyItemViewHolder policyItemViewHolder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.carlist_list_row_item, null);
            policyItemViewHolder = new PolicyItemViewHolder();
            policyItemViewHolder.carMakeTxtview=(TextView) convertView.findViewById(R.id.carlist_car_make_txtview);
            policyItemViewHolder.carModelTxtview=(TextView) convertView.findViewById(R.id.carlist_carmodel_make_txtview);
            policyItemViewHolder.carImageview=(ImageView) convertView.findViewById(R.id.carlist_imageview);

            policyItemViewHolder.carMakeTxtview.setText(policiesList.get(position).getMake());
            policyItemViewHolder.carModelTxtview.setText(" " + policiesList.get(position).getModel() + ", " + policiesList.get(position).getColor());
            policyItemViewHolder.carImageview.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_policy_car));
            convertView.setTag(policyItemViewHolder);
        } else {
            policyItemViewHolder = (PolicyItemViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(policiesList);
        super.notifyDataSetChanged();
    }

    public void clear() {
        policiesList.clear();
        notifyDataSetChanged();
    }

    public class PolicyItemViewHolder {
        private TextView carMakeTxtview;
        private TextView carModelTxtview;
        private ImageView carImageview;
    }

}
