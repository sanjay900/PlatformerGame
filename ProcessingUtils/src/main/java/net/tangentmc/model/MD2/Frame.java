package net.tangentmc.model.MD2;

import java.nio.ByteBuffer;

/**
 * Created by sanjay on 25/08/2016.
 */
public class Frame {
    private float[] scale = new float[3];
    private float[] translate = new float[3];
    private Vertex[] verticies;
    private String name;
    public Frame(ByteBuffer buffer, int vertexCount) {
        for (int i=0; i< scale.length; i++) {
            scale[i] = buffer.getFloat();
        }
        for (int i=0; i< translate.length; i++) {
            translate[i] = buffer.getFloat();
        }
        byte[] name = new byte[16];
        buffer.get(name);
        this.name = new String(name);
        verticies = new Vertex[vertexCount];
        for (int i = 0; i< vertexCount; i++) {
            verticies[i] = new Vertex(buffer,this);
        }
    }

    public float[] getScale() {
        return this.scale;
    }

    public float[] getTranslate() {
        return this.translate;
    }

    public Vertex[] getVerticies() {
        return this.verticies;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "Frame(scale=" + java.util.Arrays.toString(this.getScale()) + ", translate=" + java.util.Arrays.toString(this.getTranslate()) + ", verticies=" + java.util.Arrays.deepToString(this.getVerticies()) + ", name=" + this.getName() + ")";
    }
}
