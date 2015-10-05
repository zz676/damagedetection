package com.aig.science.damagedetection.adaptor;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.controllers.TakePhotosActivity;
import com.aig.science.damagedetection.utilities.ImageResizer;
import com.aig.science.dd3d.PartObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhisheng on 02/23/2015.
 */
public class CarPartsListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private ViewHolder holder;
    private List<String> listDataHeader;
    private HashMap<String, List<PartObject>> listDataChild;
    private static int numOfPixels;

    public CarPartsListAdapter(Context context, List<String> listDataHeader,
                               HashMap<String, List<PartObject>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.numOfPixels = TakePhotosActivity.numOfPixels;

        /*this.mInflater = LayoutInflater.from(context);
        LayoutInflater layoutInflater = (LayoutInflater) ((Activity) context)
                .getBaseContext().getSystemService(
                        context.LAYOUT_INFLATER_SERVICE);*/
    }
    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.popup_carparts_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.carparts_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final PartObject part = (PartObject) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_list_carparts, null);
        }

        holder = new ViewHolder();

        holder.cartPartName = (TextView) convertView
                .findViewById(R.id.textview_carpart_name);
        holder.carPartImgView = (ImageView) convertView.findViewById(R.id.imgview_carpartphoto_take);
        if (part.getIsPhotoTaken()) {
            holder.carPartImgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(part.getPartPhotoUrl(), (int) (numOfPixels * 1.16777), numOfPixels));
        } else {
            holder.carPartImgView.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image_available));
        }

/*        holder.carPartImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (part.getIsPhotoTaken()) {
                    Toast.makeText(context, part.getPartName(), Toast.LENGTH_SHORT).show();
                    content.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(part.getPartPhotoUrl())));
                }
            }
        });*/
        holder.cartPartName.setText(part.getPartName());
        convertView.setTag(holder);
        return convertView;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    /**
     * The Class ViewHolder.
     */
    static class ViewHolder {

        TextView cartPartName;
        ImageView carPartImgView;
    }
}
