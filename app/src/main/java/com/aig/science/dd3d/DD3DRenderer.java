package com.aig.science.dd3d;

import android.app.ProgressDialog;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.controllers.TakePhotosActivity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttrib3f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class DD3DRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "DD3DRenderer";
    private final Context mActivityContext;
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];
    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];
    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];
    private float[] mMVMatrix = new float[16];
    /**
     * Store the accumulated rotation.
     */
    private final float[] mAccumulatedRotation = new float[16];
    /**
     * Store the current rotation.
     */
    private final float[] mCurrentRotation = new float[16];
    /**
     * Store the zoom rotation.
     */
    private final float[] mCurrentZoom = new float[16];
    /**
     * A temporary matrix.
     */
    private float[] mTemporaryMatrix = new float[16];
    /**
     * A temporary matrix fro zoom.
     */
    private float[] mZoomTemporaryMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    /**
     * Store our model data in a float buffer.
     */
    private FloatBuffer mCarPositions;
    private FloatBuffer mCarNormals;
    private FloatBuffer mCarTextureCoordinates;

    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * This will be used to pass in the modelview matrix.
     */
   // private int mMVMatrixHandle;

    /**
     * This will be used to pass in the light position.
     */
    private int mLightPosHandle;

    private int mLightColorHandle;

    /**
     * This will be used to pass in the texture.
     */
    private int mTextureUniformHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int mPositionHandle;

    /**
     * This will be used to pass in model normal information.
     */
    private int mNormalHandle;

    /**
     * This will be used to pass in model texture coordinate information.
     */
    //private int mTextureCoordinateHandle;

    //private int mColorHandle;


    private int mAmbientHandle;
    private int mDiffuseHandle;
    private int mSpecularHandle;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    /**
     * Size of the position data in elements.
     */
    private final int mPositionDataSize = 3;

    /**
     * Size of the normal data in elements.
     */
    private final int mNormalDataSize = 3;

    /**
     * Size of the texture coordinate data in elements.
     */
    private final int mTextureCoordinateDataSize = 2;

    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] mLightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] mLightPosInEyeSpace = new float[4];

    /**
     * This is a handle to our cube shading program.
     */
    private int mProgramHandle;

    /**
     * This is a handle to our light point program.
     */
    private int mPointProgramHandle;

    /**
     * These are handles to our texture data.
     */
    private int mCarTextureHandle;
    private int mGrassDataHandle;


    // These still work without volatile, but refreshes are not guaranteed to happen.
    public volatile float mDeltaX;
    public volatile float mDeltaY;

    //private DrawModel model;
    private DrawModel model;
    private ProgressDialog progress;
    private int indexSize;
    private float scale = 1.0f;

    private float left;
    private float right;
    private float bottom;
    private float top;

    //Directly control the scale of the 3D model
    private float near;
    private float far;

    private int[] mViewport = new int[4];

    private List<PartObject> carparts = new ArrayList<>();
    private static float COLLISION_RADIUS = 0.1f;
    private int windowsHeight = 0;
    private int windowsWidth = 0;

    private static final String U_COLOR = "u_Color";
    private HashMap<String, PartObject> partsHashMap;

    private boolean isFirstLoading = true;
    //private ProgressDialog loadingObjDialog;

    private Vector3f mZeroVector;

    private float mEpsilon;

    private FloatBuffer lineVertices;

    private Vertex nearVertex;
    private Vertex farVertex;
    private boolean isHit = false;

    /**
     * Initialize the model data.
     */
    public DD3DRenderer(final Context activityContext) {
        mActivityContext = activityContext;
        isFirstLoading = ((TakePhotosActivity) mActivityContext).isFirstLoading();
        //loadingObjDialog = ((TakePhotosActivity) mActivityContext).getLoadingObjDialog();
/*        ((TakePhotosActivity) mActivityContext).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mActivityContext, "Hello", Toast.LENGTH_SHORT).show();
            }
        });*/
        initAllCarParts();
        //new LoadCarModelTask(mActivityContext).execute();
    }

    /**
     * read .obj file and get all parts data
     */
    public void initAllCarParts() {

        model = new DrawModel(mActivityContext, R.raw.hood_window);
        carparts = model.getAllCarParts();
       partsHashMap = model.getPartsHashMap();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Position the eye in front of the origin.
        final float eyeX = 2.5f;
        final float eyeY = 2.5f;
        final float eyeZ = -1.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -3.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 0.5f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);

        final int vertexShaderHandle = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_Normal"});

        // Load the texture
        //mCarTextureHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.car);
        //glGenerateMipmap(GL_TEXTURE_2D);

        mZeroVector = new Vector3f(0, 0, 0);
        mEpsilon = 0.001f;

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
        Matrix.setIdentityM(mCurrentZoom, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        glViewport(0, 0, width, height);

        windowsHeight = height;
        windowsWidth = width;
        mViewport = new int[]{0, 0, width, height};
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        left = -ratio;
        right = ratio;
        bottom = -1.0f;
        top = 1.0f;

        near = 0.5f;
        far = 5.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        //This will wipe out all colors on the screen and fill the screen with the color
        //previously defined by our call to glClearColor() .
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setColorForAllParts();
        drawAllCarParts();
        if (isHit) {
            //drawIntersectionLine(nearVertex, farVertex);
        }

/*        ((TakePhotosActivity) mActivityContext).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mActivityContext, "Done", Toast.LENGTH_SHORT).show();
            }
        });*/
        //drawLight();
    }


    public void setColorForAllParts() {


        int index = 1;
        float value = 1f / carparts.size();
        for (PartObject part : carparts) {
            if(!part.getIsSelected()){
                part.setColor(new Color((value * index), 0, 0));
                part.setAlpha(1.0f);
            }
            index++;
        }
    }

    /**
     * iterate over part Objects and draw them
     */
    private void drawAllCarParts() {
        for (PartObject part : carparts) {
            drawPart(part);
        }
    }

    /**
     * draw a car part
     */
    private void drawPart(PartObject part) {
        mCarPositions = model.makeFloatBuffer(part.getmVertexArray());
        mCarPositions.position(0);
        glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL_FLOAT, false,
                0, mCarPositions);
        glEnableVertexAttribArray(mPositionHandle);


/*        mCarNormals = model.makeFloatBuffer(part.getmNorArray());
        mCarNormals.position(0);
        glVertexAttribPointer(mNormalHandle, mNormalDataSize, GL_FLOAT, false,
                0, mCarNormals);
        glEnableVertexAttribArray(mNormalHandle);*/

/*        mCarTextureCoordinates = model.makeFloatBuffer(part.getmTexArray());
        mCarTextureCoordinates.position(0);*/
        indexSize = part.getmFacesArray().length;

        //mCarTextureHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.car);
        //glGenerateMipmap(GL_TEXTURE_2D);
        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        glUseProgram(mProgramHandle);
        // Set program handles for car drawing.
        mMVPMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        //mTextureUniformHandle = glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = glGetAttribLocation(mProgramHandle, "a_Position");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "lightPos");
        mLightColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "lightColor");
        mNormalHandle = glGetAttribLocation(mProgramHandle, "a_Normal");
        //mColorHandle = glGetUniformLocation(mProgramHandle, U_COLOR);

        mAmbientHandle = glGetUniformLocation(mProgramHandle, "matAmbient");
        mDiffuseHandle = glGetUniformLocation(mProgramHandle, "matDiffuse");
        mSpecularHandle = glGetUniformLocation(mProgramHandle, "matSpecular");



        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 3.5f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Translate the car model into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        //Handle Zoom in/out
        Matrix.setIdentityM(mCurrentZoom, 0);
        Matrix.scaleM(mCurrentZoom, 0, 1 / scale, 1 / scale, 1 / scale);
        Matrix.multiplyMM(mZoomTemporaryMatrix, 0, mModelMatrix, 0, mCurrentZoom, 0);
        System.arraycopy(mZoomTemporaryMatrix, 0, mModelMatrix, 0, 16);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        //glUniform1f(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        //        mCarPositions.position(0);
/*        glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GL_FLOAT, false,
                0, mCarPositions);
        glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mCarTextureHandle);*/

        // Pass in the position information

        // Pass in the normal information


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains view * model).
        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        // Pass in the modelview matrix.
        //glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains projection * view * model).
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the normal information


        // Pass in the combined matrix.
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], -2f);
        glUniform4f(mLightColorHandle, 1.0f, 1.0f, 1.0f, 1.0f);

/*        Color color = part.getColor();
        float r = 0.367059f * 0.5f;
        float g = 0.012549f * 0.5f;
        float b = 0.053333f * 0.5f;*/

        glUniform4f(mAmbientHandle,0f,0f,0f,1.0f);
        glUniform4f(mDiffuseHandle, 0.367059f, 0.012549f, 0.053333f, 1.0f);
        glUniform4f(mSpecularHandle, 0.449020f, 0.449020f, 0.449020f, 1.0f);


        //glUniform4f(mColorHandle, r, g, b, 1.0f);

        //Log.d(TAG, "Part Name:" + part.getPartName() + "; RGB:" + color.getR() + color.getG() + color.getB());
        //glUseProgram(mProgramHandle);

        // Draw the part.
        glDrawArrays(GL_TRIANGLES, 0, indexSize);
        if (isFirstLoading) {
            //loadingObjDialog.dismiss();
            isFirstLoading = false;
        }
    }

    private void drawIntersectionLine(Vertex near, Vertex far) {
/*        float[] lineVerticesData = {
                near.getX(), near.getY(), near.getZ(),
                far.getX(), far.getY(), far.getZ()
        };
        lineVertices = model.makeFloatBuffer(lineVerticesData);
        lineVertices.position(0);
        glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL_FLOAT, false, 0, lineVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        glUniform4f(mColorHandle, 1, 1, 1, 1);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);*/
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight() {
        final int pointMVPMatrixHandle = glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
        glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }

    /**
     * set the scale of zoom in/out
     *
     * @param scale
     */
    public final void zoom(float scale) {
        this.scale *= scale;
    }


    /**
     * implementation of generating vector out of 2d coordinates to check for collisions with
     * objects
     *
     * @param touchX coordinate of the touch event on the screen
     * @param touchY coordinate of the touch event on the screen
     * @return true if a collision was found, false if not
     */
    public boolean checkCollision(float touchX, float touchY) {
        touchY = mViewport[3] - touchY;
        float[] nearPoint = {0f, 0f, 0f, 0f};
        float[] farPoint = {0f, 0f, 0f, 0f};
        float[] temp1 = new float[4];
        float[] temp2 = new float[4];
        float[] rayVector = {0f, 0f, 0f};

        //Retreiving position projected on near plane
        int result = GLU.gluUnProject(touchX, touchY, -1f, mMVMatrix, 0, mProjectionMatrix, 0, mViewport, 0, nearPoint, 0);

        //Retreiving position projected on far plane
        result = GLU.gluUnProject(touchX, touchY, 1f, mMVMatrix, 0, mProjectionMatrix, 0, mViewport, 0, farPoint, 0);

        // extract 3d Coordinates put of 4d Coordinates
        nearPoint = Helper.fixW(nearPoint);
        farPoint = Helper.fixW(farPoint);

        //Processing ray vector
        rayVector[0] = farPoint[0] - nearPoint[0];
        rayVector[1] = farPoint[1] - nearPoint[1];
        rayVector[2] = farPoint[2] - nearPoint[2];

        // calculate ray vector length
        //float rayLength = (float) Math.sqrt((rayVector[0] * rayVector[0]) + (rayVector[1] * rayVector[1]) + (rayVector[2] * rayVector[2]));

        Vertex near = new Vertex(nearPoint[0], nearPoint[1], nearPoint[2]);
        Vertex far = new Vertex(farPoint[0], farPoint[1], farPoint[2]);

        double distance = 0;
        List<Triangle> disList = new ArrayList<>();
        for (PartObject part : carparts) {
            for (Triangle tri : part.getTriangleList()) {
                distance = Vertex.calculateDistanceX(near, far, tri.getCentre());
                if (distance < COLLISION_RADIUS) {
                    tri.setDistance(distance);
                    tri.setPartName(part.getPartName());
                    disList.add(tri);
                }
            }
        }

        PartObject intsectedPart = getIntersectionPart(disList, near);
        changeColorForPart(intsectedPart);
        return true;
    }

    /**
     * implementation of generating vector out of 2d coordinates to check for collisions with
     * objects
     *
     * @param touchX coordinate of the touch event on the screen
     * @param touchY coordinate of the touch event on the screen
     * @return true if a collision was found, false if not
     */
    public boolean checkCollisionX(float touchX, float touchY) {

        isHit = true;
        touchY = mViewport[3] - touchY;
        float[] nearPoint = {0f, 0f, 0f, 0f};
        float[] farPoint = {0f, 0f, 0f, 0f};
        float[] temp1 = new float[4];
        float[] temp2 = new float[4];
        float[] rayVector = {0f, 0f, 0f};

/*
        float normalizedX = 2f * touchX / mViewport[2] - 1f;
        float normalizedY = 1f - 2f * touchY / mViewport[3];
        float normalizedZ = 1.0f;

        float[] rayClip1 = new float[]{normalizedX, normalizedY, -1, 1};

        float[] rayClip2 = new float[]{normalizedX, normalizedY, 1, 1};

        float[] mVPMatrix = new float[16];
        float[] invertVPMatrix = new float[16];
        float[] invertMVPMatrix = new float[16];

        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.invertM(invertVPMatrix, 0, mVPMatrix, 0);
        Matrix.invertM(invertMVPMatrix,0,mMVPMatrix,0);
        Log.d(TAG,"getMouseRayProjection:::::Start a new click..............");
        Log.d(TAG, "Clip nearV:" + rayClip1[0] + "; "+ rayClip1[1] + "; " + rayClip1[2]);
        Log.d(TAG, "Clip farV:" + rayClip2[0] + "; "+ rayClip2[1] + "; " + rayClip2[2]);

        float[] rayWorld1 = new float[4];
        Matrix.multiplyMV(rayWorld1, 0, invertMVPMatrix, 0, rayClip1, 0);

        float[] rayWorld2 = new float[4];
        Matrix.multiplyMV(rayWorld2, 0, invertMVPMatrix, 0, rayClip2, 0);

        if (rayWorld1[3] != 0 && rayWorld2[3] != 0) {
            rayWorld1[0] = rayWorld1[0] / rayWorld1[3];
            rayWorld1[1] = rayWorld1[1] / rayWorld1[3];
            rayWorld1[2] = rayWorld1[2] / rayWorld1[3];
            rayWorld1[3] = 1;
            rayWorld2[0] = rayWorld2[0] / rayWorld2[3];
            rayWorld2[1] = rayWorld2[1] / rayWorld2[3];
            rayWorld2[2] = rayWorld2[2] / rayWorld2[3];
            rayWorld2[3] = 1;
        }
        Log.d(TAG, "World nearV:" + rayWorld1[0] + "; "+ rayWorld1[1] + "; " + rayWorld1[2]);
        Log.d(TAG, "World farV:" + rayWorld2[0] + "; "+ rayWorld2[1] + "; " + rayWorld2[2]);
        Log.d(TAG,"getMouseRayProjection:::::End!");


*/


        //Retreiving position projected on near plane
        GLU.gluUnProject(touchX, touchY, -1f, mMVMatrix, 0, mProjectionMatrix, 0, mViewport, 0, nearPoint, 0);

        //Retreiving position projected on far plane
        GLU.gluUnProject(touchX, touchY, 1f, mMVMatrix, 0, mProjectionMatrix, 0, mViewport, 0, farPoint, 0);

        //drawIntersectionLine(new Vertex(nearPoint[0], nearPoint[1], nearPoint[2]),new Vertex(farPoint[0], farPoint[1], farPoint[2]));
        Log.d(TAG,"CHEAK:::::Start a new click!");
        Log.d(TAG, "Before /w Near:" + nearPoint[0] + "; "+ nearPoint[1] + "; " + nearPoint[2]);
        Log.d(TAG, "Before /w Far:" + farPoint[0] + "; "+ farPoint[1] + "; " + farPoint[2]);

        // extract 3d Coordinates put of 4d Coordinates
        nearPoint = Helper.fixW(nearPoint);
        farPoint = Helper.fixW(farPoint);


        Log.d(TAG, "After /w Near:" + nearPoint[0] + "; "+ nearPoint[1] + "; " + nearPoint[2]);
        Log.d(TAG, "After /w Far:" + farPoint[0] + "; "+ farPoint[1] + "; " + farPoint[2]);

        Log.d(TAG,"End A Click...................!");

        nearVertex = new Vertex(nearPoint[0], nearPoint[1], nearPoint[2]);
        farVertex = new Vertex(farPoint[0], farPoint[1], farPoint[2]);

        Vector3f nearV = new Vector3f(nearPoint[0], nearPoint[1], nearPoint[2]);
        Vector3f farV = new Vector3f(farPoint[0], farPoint[1], farPoint[2]);
        Ray ray = new Ray(nearV, farV);

        RayShapeIntersection rayShapeIntersection = null;
        //double distance = 0;
        List<Triangle> disList = new ArrayList<>();
        for (PartObject part : carparts) {
            for (Triangle tri : part.getTriangleList()) {
                rayShapeIntersection = calculateIntersection(ray, tri);
                if (rayShapeIntersection.hit) {
                    //tri.setDistance(distance);
                    tri.setPartName(part.getPartName());
                    disList.add(tri);
                }
            }
        }

        PartObject intsectedPart = getIntersectionPart(disList, nearVertex);
        changeColorForPart(intsectedPart);
        return true;
    }


    public void getMouseRayProjection(float touchX, float touchY) {
        float[] rayDirection = new float[4];

        float normalizedX = 2f * touchX / mViewport[2] - 1f;
        float normalizedY = 1f - 2f * touchY / mViewport[3];
        float normalizedZ = 1.0f;

        float[] rayClip1 = new float[]{normalizedX, normalizedY, -1, 1};

        float[] rayClip2 = new float[]{normalizedX, normalizedY, 1, 1};

        float[] mVPMatrix = new float[16];
        float[] invertVPMatrix = new float[16];
        float[] invertMVPMatrix = new float[16];

        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.invertM(invertVPMatrix, 0, mVPMatrix, 0);
        Matrix.invertM(invertMVPMatrix,0,mMVPMatrix,0);
        Log.d(TAG,"getMouseRayProjection:::::Start a new click!");
        Log.d(TAG, "Clip nearV:" + rayClip1[0] + "; "+ rayClip1[1] + "; " + rayClip1[2]);
        Log.d(TAG, "Clip farV:" + rayClip2[0] + "; "+ rayClip2[1] + "; " + rayClip2[2]);

        float[] rayWorld1 = new float[4];
        Matrix.multiplyMV(rayWorld1, 0, invertMVPMatrix, 0, rayClip1, 0);

        float[] rayWorld2 = new float[4];
        Matrix.multiplyMV(rayWorld2, 0, invertMVPMatrix, 0, rayClip2, 0);

        if (rayWorld1[3] != 0 && rayWorld2[3] != 0) {
            rayWorld1[0] = rayWorld1[0] / rayWorld1[3];
            rayWorld1[1] = rayWorld1[1] / rayWorld1[3];
            rayWorld1[2] = rayWorld1[2] / rayWorld1[3];
            rayWorld1[3] = 1;
            rayWorld2[0] = rayWorld2[0] / rayWorld2[3];
            rayWorld2[1] = rayWorld2[1] / rayWorld2[3];
            rayWorld2[2] = rayWorld2[2] / rayWorld2[3];
            rayWorld2[3] = 1;
        }

        nearVertex = new Vertex(rayWorld1[0], rayWorld1[1], rayWorld1[2]);
        farVertex = new Vertex(rayWorld2[0], rayWorld2[1], rayWorld2[2]);

        Vector3f nearV = new Vector3f(rayWorld1[0], rayWorld1[1], rayWorld1[2]);
        Vector3f farV = new Vector3f(rayWorld2[0], rayWorld2[1], rayWorld2[2]);

        Log.d(TAG, "World nearV:" + rayWorld1[0] + "; "+ rayWorld1[1] + "; " + rayWorld1[2]);
        Log.d(TAG, "World farV:" + rayWorld2[0] + "; "+ rayWorld2[1] + "; " + rayWorld2[2]);
        Log.d(TAG,"getMouseRayProjection:::::End!");
        Ray ray = new Ray(nearV, farV);

        RayShapeIntersection rayShapeIntersection = null;
        //double distance = 0;
        List<Triangle> disList = new ArrayList<>();
        for (PartObject part : carparts) {
            for (Triangle tri : part.getTriangleList()) {
                rayShapeIntersection = calculateIntersection(ray, tri);
                if (rayShapeIntersection.hit) {
                    //tri.setDistance(distance);
                    tri.setPartName(part.getPartName());
                    disList.add(tri);
                }
            }
        }

        //PartObject intsectedPart = getIntersectionPart(disList, nearVertex);
        //changeColorForPart(intsectedPart);
    }



    /**
     * change color for the selected part
     *
     * @param insectedPart
     */
    public void changeColorForPart(PartObject insectedPart) {

        //mCarTextureHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.green);
        //glGenerateMipmap(GL_TEXTURE_2D);
        if (insectedPart == null) {
            //Toast.makeText(mActivityContext, "nothing is selected", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, insectedPart.getPartName());
            for (PartObject part : carparts) {
                if (part.getPartName().equals(insectedPart.getPartName())) {
                    if (part.getIsSelected()) {
                        part.setIsSelected(false);
                    } else {
                        part.setIsSelected(true);
                        ((TakePhotosActivity) mActivityContext).setCurrentPart(part);
                        ((TakePhotosActivity) mActivityContext).handlePartSelectedClick();
                    }
                } else {
                    part.setIsSelected(false);
                }
                part.fireAction();
            }
            //Toast.makeText(mActivityContext, insectedPart.getPartName(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * find out intersected part
     *
     * @param disList
     * @param near
     * @return
     */
    public PartObject getIntersectionPart(List<Triangle> disList, Vertex near) {

        PartObject part = null;
        String partName = "";
        double minDis = Double.MAX_VALUE;
        for (Triangle tri : disList) {
            double dis = Vertex.calculateDistanceBetweemPoints(tri.getCentre(), near);
            if (dis < minDis) {
                minDis = dis;
                partName = tri.getPartName();
            }
        }
        part = partsHashMap.get(partName);
        return part;
    }

    public float[] unProject(float x, float y, float z) {
        //x = windowsWidth - x;
        y = windowsHeight - y;

        float[] normalizedInPoint = new float[4];
        float[] mModelPoint = new float[4];

        float[] mInvertedMVPmatrix = new float[16];
        Matrix.invertM(mInvertedMVPmatrix, 0, mMVPMatrix, 0);

        normalizedInPoint[0] = (x / windowsWidth) * 2 - 1;
        normalizedInPoint[1] = (y / windowsHeight) * 2 - 1;
        normalizedInPoint[2] = 2 * z - 1;
        normalizedInPoint[3] = 1;

        Matrix.multiplyMV(mModelPoint, 0, mInvertedMVPmatrix, 0, normalizedInPoint, 0);

        if (mModelPoint[3] == 0)
            return null;

        float[] result = new float[3];
        result[0] = mModelPoint[0] / mModelPoint[3];
        result[1] = mModelPoint[1] / mModelPoint[3];
        result[2] = mModelPoint[2] / mModelPoint[3];
        return result;
    }

    /**
     * Helper method to calculate the intersection between a ray and a
     * triangle..
     *
     * @param ray      the ray
     * @param triangle the triangle
     * @return the ray shape intersection
     */
    private RayShapeIntersection calculateIntersection(Ray ray,
                                                       Triangle triangle) {
        RayShapeIntersection intersection = new RayShapeIntersection();
        Vector3f u = new Vector3f();
        Vector3f v = new Vector3f();

        Point3f point1 = new Point3f(triangle.getVertexX().getX(), triangle.getVertexX().getY(), triangle.getVertexX().getZ());
        Point3f point2 = new Point3f(triangle.getVertexY().getX(), triangle.getVertexY().getY(), triangle.getVertexY().getZ());
        Point3f point3 = new Point3f(triangle.getVertexZ().getX(), triangle.getVertexZ().getY(), triangle.getVertexZ().getZ());

        u.sub(point2, point1);
        v.sub(point3, point1);

        Vector3f n = new Vector3f();
        n.cross(u, v);

        if (n.epsilonEquals(mZeroVector, mEpsilon)) {
            // Triangle is either a point or a line (degenerate)
            // Don't deal with this case
            return intersection;
        }

        Vector3f w0 = new Vector3f();
        w0.sub(ray.getOrigin(), point1);

        float a = -n.dot(w0);
        float b = n.dot(ray.getDirection());
        if (Math.abs(b) < mEpsilon) { // ray is parallel to triangle plane
            if (a == 0) {
                // ray lies in triangle plane
                return intersection;
            } else {
                // ray disjoint from planeRayShapeIntersection
                return intersection;
            }
        }

        float r = a / b;
        if (r < 0.f) {
            // ray goes away from triangle
            return intersection;
        }

        intersection.hit = true;
        intersection.hitPoint = new Vector3f();
        intersection.hitPoint.scaleAdd(r, ray.getDirection(), ray.getOrigin());

        // Is intersection inside the triangle?
        Vector3f w = new Vector3f();
        w.sub(intersection.hitPoint, point1);
        float uu, uv, vv, wu, wv, D;
        uu = u.dot(u);
        uv = u.dot(v);
        vv = v.dot(v);
        wu = w.dot(u);
        wv = w.dot(v);
        D = uv * uv - uu * vv;

        // get and test parametric coordinates
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.f || s > 1) {
            // I is outside T
            intersection.hit = false;
            intersection.hitPoint = null;
            return intersection;
        }

        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0) {
            // I is outside T
            intersection.hit = false;
            intersection.hitPoint = null;
            return intersection;
        }
        return intersection;
    }


    /**
     * get method for model
     *
     * @return model
     */
    public DrawModel getModel() {
        return model;
    }

    public List<PartObject> getCarparts() {
        return carparts;
    }
}