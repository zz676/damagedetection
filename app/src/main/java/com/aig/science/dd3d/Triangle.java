package com.aig.science.dd3d;

/**
 * Created by researcher on 2/19/15.
 */
public class Triangle {

    private int index;
    private Vertex vertexX;
    private Vertex vertexY;
    private Vertex vertexZ;
    private Vertex centre;
    private double distance;

    public void setPartName(String partName) {
        this.partName = partName;
    }

    private String partName;

    public Triangle(Vertex x, Vertex y, Vertex z) {
        this.vertexX = x;
        this.vertexY = y;
        this.vertexZ = z;
    }


    public String getPartName() {
        return partName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Vertex getVertexX() {
        return vertexX;
    }

    public void setVertexX(Vertex vertexX) {
        this.vertexX = vertexX;
    }

    public Vertex getVertexY() {
        return vertexY;
    }

    public void setVertexY(Vertex vertexY) {
        this.vertexY = vertexY;
    }

    public Vertex getVertexZ() {
        return vertexZ;
    }

    public void setVertexZ(Vertex vertexZ) {
        this.vertexZ = vertexZ;
    }

    public Vertex getCentre() {
        return centre;
    }

    public void setCentre(Vertex centre) {
        this.centre = centre;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


}
