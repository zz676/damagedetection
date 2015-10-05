package com.aig.science.damagedetection.adaptor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aig.science.damagedetection.utilities.ItemViewHolder;
import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.models.Place;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopsListViewAdapter extends BaseAdapter {

    private final String TAG = "ShopsListViewAdapter";

    private Context mContext;
    private LayoutInflater infalter;
    private final List<Place> shopsList;

    public ShopsListViewAdapter(Context context, List<Place> shopsList) {
        infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.shopsList = shopsList;
    }

    @Override
    public int getCount() {
        return shopsList.size();
    }

    @Override
    public Place getItem(int position) {
        return shopsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addAll(ArrayList<Place> places) {

        try {
            this.shopsList.clear();
            this.shopsList.addAll(places);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
        notifyDataSetChanged();
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.shops_list_row_item,parent,false);
        TextView nameTextView = (TextView)rowView.findViewById(R.id.shop_name_txtview);
        TextView addressTextView = (TextView)rowView.findViewById(R.id.shop_address_txtview);
        TextView phoneNumTextView = (TextView)rowView.findViewById(R.id.phone_number_txtview);
        ImageView shopImageView = (ImageView)rowView.findViewById(R.id.shop_imageview);
        TextView distanceTextView = (TextView)rowView.findViewById(R.id.distance_txtview);


        nameTextView.setText(shopsList.get(position).getsName());
        addressTextView.setText(shopsList.get(position).getsPhoneNumber());
        phoneNumTextView.setText(shopsList.get(position).getsVicinity());
        distanceTextView.setText(String.valueOf(shopsList.get(position).getDistance()) + " miles");
        shopImageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_repair_shop));

/*        final ItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.shops_list_row_item, null);
            viewHolder = new ItemViewHolder();
            viewHolder.setShopNameTextView((TextView) convertView.findViewById(R.id.shop_name_txtview));
            viewHolder.setShopAddressTextView((TextView) convertView.findViewById(R.id.shop_address_txtview));
            viewHolder.setPhoneNumTextView((TextView) convertView.findViewById(R.id.phone_number_txtview));
            viewHolder.setShopImageView((ImageView) convertView.findViewById(R.id.shop_imageview));
            viewHolder.setDistanceTextView((TextView) convertView.findViewById(R.id.distance_txtview));

            viewHolder.getShopNameTextView().setText(shopsList.get(position).getsName());
            viewHolder.getPhoneNumTextView().setText(shopsList.get(position).getsPhoneNumber());
            viewHolder.getShopAddressTextView().setText(shopsList.get(position).getsVicinity());
            viewHolder.getDistanceTextView().setText(String.valueOf(shopsList.get(position).getDistance()) + " miles");
            viewHolder.getShopImageView().setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_repair_shop));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }
        return convertView;*/
        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    public void clear() {
        shopsList.clear();
        notifyDataSetChanged();
    }
}
