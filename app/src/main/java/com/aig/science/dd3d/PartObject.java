package com.aig.science.dd3d;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by researcher on 2/11/15.
 */
public class PartObject {


    private final static String LOG = "PartObject";

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    private float alpha = 1.0f;
    private Boolean isPhotoTaken = false;
    private Boolean isSelected = false;

    public void setColor(Color color) {
        this.color = color;
    }

    private Color color = new Color(1, 0, 0);
    private String partName;
    private float[] mVertexArray;
    private float[] mTexArray;
    private float[] mNorArray;
    private short[] mFacesArray;
    private String partPhotoUrl = "";
    private List<Triangle> triangleList = new ArrayList<>();

    public PartObject(String partName) {
        this.partName = partName;
    }

    public float getAlpha() {
        return alpha;
    }

    public Color getColor() {
        return color;
    }

    public Boolean getIsPhotoTaken() {
        return isPhotoTaken;
    }

    public void setIsPhotoTaken(Boolean isPhotoTaken) {
        this.isPhotoTaken = isPhotoTaken;
    }

    public short[] getmFacesArray() {
        return mFacesArray;
    }

    public void setmFacesArray(short[] mFacesArray) {
        this.mFacesArray = mFacesArray;
    }

    public String getPartName() {
        return partName;
    }

    public float[] getmVertexArray() {
        return mVertexArray;
    }

    public void setmVertexArray(float[] mVertexArray) {
        this.mVertexArray = mVertexArray;
    }

    public float[] getmTexArray() {
        return mTexArray;
    }

    public void setmTexArray(float[] mTexArray) {
        this.mTexArray = mTexArray;
    }

    public float[] getmNorArray() {
        return mNorArray;
    }

    public void setmNorArray(float[] mNorArray) {
        this.mNorArray = mNorArray;
    }

    public List<Triangle> getTriangleList() {
        return triangleList;
    }

    public void setTriangleList(List<Triangle> triangleList) {
        this.triangleList = triangleList;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getPartPhotoUrl() {
        return partPhotoUrl;
    }

    public void setPartPhotoUrl(String partPhotoUrl) {
        this.partPhotoUrl = partPhotoUrl;
    }

    /**
     * change the luminance of a part after selected/unselected
     */

    public void afterSelected() {

        Log.d(LOG, "Selected");
        if (this.alpha == 1.0f) {
            this.alpha = 0.5f;
        } else {
            this.alpha = 1.0f;
        }
    }

   public void fireAction() {
        Log.d(LOG, "Touched");
/*        if (this.alpha == 1f) {
            this.alpha = 0.5f;
            this.color = new Color(1, 0, 0);
        } else {
            this.alpha = 1f;
            this.color = new Color(0, 0, 1);
        }*/

        if (this.isSelected == true) {
            this.color = new Color(0, 0, 1);
        } else {
            this.color = new Color(1, 0, 0);
        }
    }
}
