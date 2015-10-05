package com.aig.science.damagedetection.helper;

import android.hardware.Camera;

import com.aig.science.damagedetection.logger.Log;

/**
 * Used to make camera use in the tutorial a bit more obvious
 * in a production environment you wouldn't make these calls static
 * as you have no way to mock them for testing
 * @author paul.blundell
 *
 */
public class CameraHelper {

	public static boolean cameraAvailable(Camera camera) {
		return camera != null;
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			// Camera is not available or doesn't exist
			Log.d("getCamera failed", e.getMessage());
		}
		return c;
	}

}
