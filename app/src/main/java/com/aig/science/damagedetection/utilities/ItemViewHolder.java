package com.aig.science.damagedetection.utilities;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zhizhou on 11/3/2014.
 */
public class ItemViewHolder {
    private TextView shopNameTextView;
    private TextView phoneNumTextView;
    private TextView shopAddressTextView;
    private TextView distanceTextView;
    private ImageView shopImageView;

    public void setShopNameTextView(TextView shopNameTextView) {
        this.shopNameTextView = shopNameTextView;
    }

    public void setPhoneNumTextView(TextView phoneNumTextView) {
        this.phoneNumTextView = phoneNumTextView;
    }

    public void setShopAddressTextView(TextView shopAddressTextView) {
        this.shopAddressTextView = shopAddressTextView;
    }

    public void setDistanceTextView(TextView distanceTextView) {
        this.distanceTextView = distanceTextView;
    }

    public void setShopImageView(ImageView shopImageView) {
        this.shopImageView = shopImageView;
    }

    public TextView getShopNameTextView() {
        return shopNameTextView;
    }

    public TextView getPhoneNumTextView() {
        return phoneNumTextView;
    }

    public TextView getShopAddressTextView() {
        return shopAddressTextView;
    }

    public TextView getDistanceTextView() {
        return distanceTextView;
    }

    public ImageView getShopImageView() {
        return shopImageView;
    }
}
