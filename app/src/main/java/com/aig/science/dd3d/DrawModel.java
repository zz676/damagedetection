package com.aig.science.dd3d;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;

import com.aig.science.damagedetection.logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DrawModel {
    private final int NUM_FACE_VERTICES = 3;
    private final int NUM_VERTEX_COORDS = 3;
    private final int NUM_NORMAL_COORDS = 3;
    private final int NUM_TEX_COORDS = 2;
    private final FloatBuffer mVertexBuffer;
    private final ShortBuffer mIndexBuffer;
    private final FloatBuffer mTexBuffer;
    private final FloatBuffer mNorBuffer;
    private final int indeSize;
    private List<PartObject> allCarParts= new ArrayList<>();
    HashMap<String,PartObject> partsHashMap = new HashMap<>();

    public DrawModel(Context context, int resId) {

        // read in all the lines and put in their respective arraylists of strings
        // reason I do this is to get a count of the faces to be used to initialize the
        // float arrays
        ArrayList<Line> vertexes = new ArrayList<Line>();
        ArrayList<Line> textures = new ArrayList<Line>();
        ArrayList<Line> faces = new ArrayList<Line>();
        ArrayList<Line> normals = new ArrayList<Line>();
        HashMap<String,Integer> partSizeMap = new HashMap<>();
        InputStream iStream = context.getResources().openRawResource(resId);
        InputStreamReader isr = new InputStreamReader(iStream);
        BufferedReader bReader = new BufferedReader(isr);
        String line;
        String previousPartName = "";
        String partName = "";
        int partFaceSize = 0;
        try {
            while ((line = bReader.readLine()) != null) {

                if(line.startsWith("o ")){
                    String temp = line.split(" ")[1];
                    if(temp.contains("_name")){
                        partName = (temp.substring(0, temp.indexOf("_name"))).replace("_"," ");
                    } else {
                        partName = temp;
                    }
                }
                // do not read in the leading v, vt or f
                if (line.startsWith("v ")) {
                    vertexes.add(new Line(partName, line.substring(2)));
                }
                if (line.startsWith("vt ")) {
                    textures.add(new Line(partName, line.substring(3)));
                }
                if (line.startsWith("f ")) {
                    if(!partName.equals(previousPartName)){
                        if(!previousPartName.equals("")){
                            partSizeMap.put(previousPartName,partFaceSize);
                        }
                        previousPartName = partName;
                        partFaceSize = 1;
                    } else {
                        partFaceSize++;
                    }
                    faces.add(new Line(partName, line.substring(2)));
                }
                if (line.startsWith("vn ")) {
                    normals.add(new Line(partName, line.substring(3)));
                }
            }
            if(!partName.equals("")){
                partSizeMap.put(previousPartName,partFaceSize);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        // holding arrays for the vertices, texture coords and indexes
        float[] vCoords = new float[faces.size() * NUM_FACE_VERTICES * NUM_VERTEX_COORDS];
        float[] tCoords = new float[faces.size() * NUM_FACE_VERTICES * NUM_TEX_COORDS];
        float[] nCoords = new float[faces.size() * NUM_FACE_VERTICES * NUM_NORMAL_COORDS];
        short[] iCoords = new short[faces.size() * NUM_FACE_VERTICES];

        float[] vPartCoords = null;
        float[] tPartCoords = null;
        float[] nPartCoords = null;
        short[] iPartCoords = null;

        int vertexIndex = 0;
        int faceIndex = 0;
        int textureIndex = 0;
        int normalIndex = 0;

        int partVertexIndex = 0;
        int partFaceIndex = 0;
        int partTextureIndex = 0;
        int partNormalIndex = 0;
        // for each face
        String nameOfPart = "";
        PartObject part = null;
        int faceSize = 1;
        for (Line i : faces) {

            if(nameOfPart.equals("")){
                nameOfPart = i.partName;
                part = new PartObject(nameOfPart);
                vPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_VERTEX_COORDS];
                tPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_TEX_COORDS];
                nPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_NORMAL_COORDS];
                iPartCoords = new short[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES];
            } else {
                if(!i.partName.equals(nameOfPart)){
                    part = new PartObject(nameOfPart);
                    part.setmVertexArray(vPartCoords);
                    part.setmTexArray(tPartCoords);
                    part.setmNorArray(nPartCoords);
                    part.setmFacesArray(iPartCoords);
                    part.setTriangleList(makeTriangles(vPartCoords));
                    allCarParts.add(part);
                    partsHashMap.put(nameOfPart, part);
                    nameOfPart = i.partName;
                    partVertexIndex = 0;
                    partFaceIndex = 0;
                    partTextureIndex = 0;
                    partNormalIndex = 0;
                    vPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_VERTEX_COORDS];
                    tPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_TEX_COORDS];
                    nPartCoords = new float[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES * NUM_NORMAL_COORDS];
                    iPartCoords = new short[partSizeMap.get(nameOfPart) * NUM_FACE_VERTICES];
                }
            }
            // for each face component
            for (String j : i.context.split(" ")) {
                iCoords[faceIndex] = (short) faceIndex++;
                iPartCoords[partFaceIndex] = (short) partFaceIndex++;
                String[] faceComponent = j.split("/");

                String vertex = vertexes.get(Integer.parseInt(faceComponent[0]) - 1).context;
                String texture = textures.get(Integer.parseInt(faceComponent[1]) - 1).context;
                String normal = normals.get(Integer.parseInt(faceComponent[2]) - 1).context;
                String vertexComp[] = vertex.split(" ");
                String textureComp[] = texture.split(" ");
                String normalComp[] = normal.split(" ");

                for (String v : vertexComp) {
                    vCoords[vertexIndex++] = Float.parseFloat(v);
                    vPartCoords[partVertexIndex++] = Float.parseFloat(v);
                }

                for (String t : textureComp) {
                    tCoords[textureIndex++] = Float.parseFloat(t);
                    tPartCoords[partTextureIndex++] = Float.parseFloat(t);
                }

                for (String n : normalComp) {
                    nCoords[normalIndex++] = Float.parseFloat(n);
                    nPartCoords[partNormalIndex++] = Float.parseFloat(n);
                }
            }
            if(faceSize == faces.size()){
                part = new PartObject(nameOfPart);
                part.setmVertexArray(vPartCoords);
                part.setmTexArray(tPartCoords);
                part.setmNorArray(nPartCoords);
                part.setmFacesArray(iPartCoords);
                part.setTriangleList(makeTriangles(vPartCoords));
                allCarParts.add(part);
                partsHashMap.put(nameOfPart, part);
                nameOfPart = i.partName;
            }
            faceSize++;
        }

        // create the final buffers
        mVertexBuffer = makeFloatBuffer(vCoords);
        mIndexBuffer = makeShortBuffer(iCoords);
        mNorBuffer = makeFloatBuffer(nCoords);
        mTexBuffer = makeFloatBuffer(tCoords);
        indeSize = iCoords.length;
    }

    //make tirangles and calculate its center
    private List<Triangle> makeTriangles(float[] vPartCoords){
        List<Triangle> triangles = new ArrayList<>();
        int index = 0;
        Triangle tri;
        Vertex point1 ;
        Vertex point2 ;
        Vertex point3 ;
        Vertex center;
        while(index+8<vPartCoords.length){
            point1= new Vertex(vPartCoords[index],vPartCoords[index+1],vPartCoords[index+2]);
            point2= new Vertex(vPartCoords[index+3],vPartCoords[index+4],vPartCoords[index+5]);
            point3= new Vertex(vPartCoords[index+6],vPartCoords[index+7],vPartCoords[index+8]);
            tri = new Triangle(point1,point2,point3);
            center = new Vertex((point1.getX()+point2.getX()+point3.getX())/3,(point1.getY()+point2.getY()+point3.getY())/3,(point1.getZ()+point2.getZ()+point3.getZ())/3);
            tri.setCentre(center);
            triangles.add(tri);
            index += 9;
        }
        return triangles;
    }

    public FloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    public ShortBuffer makeShortBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    /*public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(NUM_VERTEX_COORDS, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glTexCoordPointer(NUM_TEX_COORDS, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.remaining(), GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }*/


    public FloatBuffer getmVertexBuffer() {
        return mVertexBuffer;
    }

    public ShortBuffer getmIndexBuffer() {
        return mIndexBuffer;
    }

    public FloatBuffer getmTexBuffer() {
        return mTexBuffer;
    }

    public FloatBuffer getmNorBuffer() {
        return mNorBuffer;
    }

    public int getIndeSize() {
        return indeSize;
    }

    public List<PartObject> getAllCarParts() {
        return allCarParts;
    }
    public HashMap<String, PartObject> getPartsHashMap() {
        return partsHashMap;
    }
    class Line{
        private String partName;
        private String context;
        public Line(String partName, String context){

            this.partName = partName;
            this.context = context;
        }
    }
}

