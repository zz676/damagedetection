package com.aig.science.dd3d;

/**
 * Created by researcher on 2/19/15.
 */
public class Vertex {
    private float x;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    private float y;
    private float z;

    public Vertex(){}

    public Vertex(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /*
        calculate the distance between a point and a line in 3d
     */
    public static double calculateDistance(Vertex start, Vertex end, Vertex point) {

        Vertex v = new Vertex();
        v.x = end.x - start.x;
        v.y = end.y - start.y;
        v.z = end.z - start.z;

        Vertex vLine = new Vertex();
        vLine.x = end.x - point.x;
        vLine.y = end.y - point.y;
        vLine.z = end.z - point.z;

        Vertex temp = new Vertex();
        temp.x = vLine.y * v.z - vLine.z * v.y;
        temp.y = vLine.z * v.x - vLine.x * v.z;
        temp.z = vLine.x * v.y - vLine.y * v.x;

        double distance = Math.sqrt(temp.x * temp.x + temp.y * temp.y + temp.z * temp.z) / Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return distance;
    }

    public static double calculateDistanceX(Vertex start, Vertex end, Vertex point) {

        Vertex v = new Vertex();
        v.x = end.x - start.x;
        v.y = end.y - start.y;
        v.z = end.z - start.z;

        Vertex temp = new Vertex();
        temp.x = (point.y - start.y) * (point.z - start.z) - (point.y - end.y) * (point.z - start.z);
        temp.y = (point.x - start.x) * (point.z - start.z) - (point.x - end.x) * (point.z - start.z);
        temp.z = (point.x - start.x) * (point.y - end.y) - (point.x - end.x) * (point.y - start.y);

        double distance = Math.sqrt(temp.x * temp.x + temp.y * temp.y + temp.z * temp.z) / Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);

        return distance;
    }

    /*
        calculate the distance between two points in 3d
    */
    public static double calculateDistanceBetweemPoints(Vertex point1, Vertex point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y) + (point1.z - point2.z) * (point1.z - point2.z));
    }
}
