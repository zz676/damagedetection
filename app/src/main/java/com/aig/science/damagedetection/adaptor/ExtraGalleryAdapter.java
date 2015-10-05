package com.aig.science.damagedetection.adaptor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aig.science.damagedetection.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class ExtraGalleryAdapter extends BaseAdapter {

    private final String TAG = "ExtraGalleryAdapter";

    private Context mContext;
    private LayoutInflater infalter;
    private List<String> imagePathList = new ArrayList<String>();
    ImageLoader imageLoader;

    private boolean isActionMultiplePick;

    public ExtraGalleryAdapter(Context context, List<String> imagePathList) {
        infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        return imagePathList.size();
    }

    @Override
    public String getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addAll(ArrayList<String> files) {

        try {
            this.imagePathList.clear();
            this.imagePathList.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //gridView = new View(mContext);
            convertView = infalter.inflate(R.layout.gridview_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gridview_imgQueue);
            if(imagePathList.size()>0){
                imageView.setImageURI(Uri.parse(imagePathList.get(position)));
            }
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
    }

    public void clearCache() {
        imageLoader.clearDiscCache();
        imageLoader.clearMemoryCache();
    }

    public void clear() {
        imagePathList.clear();
        notifyDataSetChanged();
    }
}
