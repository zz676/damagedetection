package com.aig.science.dd3d;
import javax.vecmath.Vector3f;

/**
 * This is a data structure to hold information about whether an intersection
 * between a ray and a shape occured and if yes where the hit point is.
 */
public class RayShapeIntersection {
	
	/** The hit point between the shape and the ray. */
	public Vector3f hitPoint;
	
	/** A boolean that indicates whether a hit actually occurred. */
	public boolean hit;
	

	/**
	 * Instantiates a new ray shape intersection.
	 */
	public RayShapeIntersection() {
		hitPoint = null;
		hit = false;
	}

	/**
	 * Instantiates a new ray shape intersection.
	 * 
	 * @param hit
	 *            A boolean indicating whether a hit occurred
	 * @param hitPoint
	 *            the hit point between the ray and the shape
	 */
	public RayShapeIntersection(boolean hit, Vector3f hitPoint) {
		this.hit = hit;
		this.hitPoint = hitPoint;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (hit) {
			return "Hitpoint: " + hitPoint.toString();
		} else {
			return "No hit";
		}
	}
}