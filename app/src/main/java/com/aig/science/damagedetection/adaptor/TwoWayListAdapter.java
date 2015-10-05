package com.aig.science.damagedetection.adaptor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.utilities.ImageResizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhizhou on 11/6/2014.
 */

public class TwoWayListAdapter extends BaseAdapter {
    private final Context mContext;
    private static int widthOfImage;
    private static int heightOfImage;
    private List<String> imagePathList = new ArrayList<String>();

    public TwoWayListAdapter(Context context, List<String> imagePathList) {
        mContext = context;
        this.imagePathList = imagePathList;
        widthOfImage = (int) ImageResizer.convertDpToPixel(155, context);
        heightOfImage = (int) ImageResizer.convertDpToPixel(225, context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.twoway_list_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.twoway_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(imagePathList.get(position), widthOfImage, heightOfImage));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, imagePathList.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(imagePathList.get(position))),"image/*");
                mContext.startActivity(intent);
                //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content:/" + imagePathList.get(position))));
            }
        });

        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
    }

    public void clear() {
        imagePathList.clear();
        notifyDataSetChanged();
    }
}
