package net.tangentmc.model.MD2;

import processing.core.PVector;

import java.nio.ByteBuffer;

/**
 * Created by sanjay on 25/08/2016.
 */
public class Vertex {
    private PVector vert;
    private int lightnormalindex;
    public Vertex(PVector vert, int lightnormalindex) {
        this.vert = vert;
        this.lightnormalindex = lightnormalindex;
    }
    public Vertex(ByteBuffer buf, Frame frame) {
        int[] v = new int[3];
        for (int i = 0; i < v.length; i++) {
            v[i] = buf.get()& 0xff;
        }
        vert = new PVector((v[0] * frame.getScale()[0]) + frame.getTranslate()[0],(v[1] * frame.getScale()[1]) + frame.getTranslate()[1],(v[2] * frame.getScale()[2]) + frame.getTranslate()[2]);
        lightnormalindex = buf.get()& 0xff;
    }

    public PVector getVert() {
        return this.vert;
    }

    public int getLightnormalindex() {
        return this.lightnormalindex;
    }

    public String toString() {
        return "Vertex(vert=" + this.getVert() + ", lightnormalindex=" + this.getLightnormalindex() + ")";
    }
}
