package com.aig.science.dd3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

public class DD3DGLSurfaceView extends GLSurfaceView {
    private DD3DRenderer mRenderer;

    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;
    private int touchState = 0;

    float oldDistance = 0.0f;

    private long touchStartDate;
    private long moveStartDate;
    private Boolean touched = false;
    private boolean moving = false;
    private static final long MOVE_INTERVAL = 100;
    private static final long CLICK_TIMEOUT = 500;


    public DD3DGLSurfaceView(Context context) {
        super(context);
    }

    public DD3DGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            final int action = MotionEventCompat.getActionMasked(event);
            //switch (event.getAction() & MotionEvent.ACTION_MASK) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (mRenderer != null) {
                        if (touchState == 1 && event.getPointerCount() == 2) {
                            float newDist = fingerDist(event);
                            float scale = oldDistance / newDist;
                            mRenderer.zoom(scale);
                            oldDistance = newDist;

                        } else {

                            float deltaX = (x - mPreviousX) / mDensity / 2f;
                            float deltaY = (y - mPreviousY) / mDensity / 2f;
                            mRenderer.mDeltaX += deltaX;
                            mRenderer.mDeltaY += deltaY;
                        }
                    }
                    moving = true;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchState = 1; // pinch the model
                    oldDistance = fingerDist(event);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if(event.getPointerCount() < 2){
                        touchState = 0;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL: {
                    break;
                }

                case MotionEvent.ACTION_UP:{

                    if (!moving || System.currentTimeMillis() - touchStartDate < CLICK_TIMEOUT){

                        mRenderer.checkCollisionX(event.getX(), event.getY());
                        //mRenderer.getMouseRayProjection(event.getX(), event.getY());
                        //mRenderer.checkCollision(event.getX(), event.getY());
                        //mRenderer.unProject(event.getX(),event.getY(),1);
                        //mRenderer.getMouseRayProjection(event.getX(), event.getY());
                    }
                    touched = false;
                    moving = false;
                    touchStartDate = 0;
                    moveStartDate = 0;
                    break;
                }

                case MotionEvent.ACTION_DOWN:{

                    touchStartDate = System.currentTimeMillis();
                    moveStartDate = 0;
                    touched = true;
                    moving  = false;
                    break;
                }
            }
            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    protected final float fingerDist(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    // Hides superclass method.
    public void setRenderer(DD3DRenderer renderer, float density) {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
        // super.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
