package net.tangentmc.model.MD2;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The header for a MD2 model
 */
class Header {
    private String ident;
    private int version;
    private int skinwidth;
    private int skinheight;
    private int framesize;
    private int skinCount;
    private int vertexCount;
    private int UVCount;
    private int triCount;
    private int glCmdCount;
    private int frameCount;
    private int skinOffset;
    private int UVOffset;
    private int triOffset;
    private int frameOffset;
    private int glCmdOffset;
    private int endOffset;

    Header(ByteBuffer buf) throws IOException {
        byte[] identBytes = new byte[4];
        buf.get(identBytes);
        ident = new String(identBytes);
        //The magic number for a MD2 File is IDP2. Checking this means that we can throw a nice exception
        //If a non md2 file is opened.
        if (!ident.equals("IDP2")) throw new IOException("This file has an incorrect magic number!");
        version = buf.getInt();
        if (version != 8) throw new IOException("This file has an incorrect version!");
        //Read and assign some values.
        skinwidth = buf.getInt();
        skinheight = buf.getInt();

        framesize = buf.getInt();

        skinCount = buf.getInt();
        vertexCount = buf.getInt();
        UVCount = buf.getInt();
        triCount = buf.getInt();
        glCmdCount = buf.getInt();
        frameCount = buf.getInt();

        //Where about in the file are we reading things from?
        skinOffset = buf.getInt();
        UVOffset = buf.getInt();
        triOffset = buf.getInt();
        frameOffset = buf.getInt();
        glCmdOffset = buf.getInt();
        endOffset = buf.getInt();
    }

    public int getSkinwidth() {
        return skinwidth;
    }

    public int getSkinheight() {
        return skinheight;
    }

    int getSkinCount() {
        return this.skinCount;
    }

    int getVertexCount() {
        return this.vertexCount;
    }

    int getUVCount() {
        return this.UVCount;
    }

    int getTriCount() {
        return this.triCount;
    }

    int getGlCmdCount() {
        return this.glCmdCount;
    }

    int getFrameCount() {
        return this.frameCount;
    }

    int getSkinOffset() {
        return this.skinOffset;
    }

    int getUVOffset() {
        return this.UVOffset;
    }

    int getTriOffset() {
        return this.triOffset;
    }

    int getFrameOffset() {
        return this.frameOffset;
    }

    int getGlCmdOffset() {
        return this.glCmdOffset;
    }

    @Override
    public String toString() {
        return "Header{" +
                "ident='" + ident + '\'' +
                ", version=" + version +
                ", skinwidth=" + skinwidth +
                ", skinheight=" + skinheight +
                ", framesize=" + framesize +
                ", skinCount=" + skinCount +
                ", vertexCount=" + vertexCount +
                ", UVCount=" + UVCount +
                ", triCount=" + triCount +
                ", glCmdCount=" + glCmdCount +
                ", frameCount=" + frameCount +
                ", skinOffset=" + skinOffset +
                ", UVOffset=" + UVOffset +
                ", triOffset=" + triOffset +
                ", frameOffset=" + frameOffset +
                ", glCmdOffset=" + glCmdOffset +
                ", endOffset=" + endOffset +
                '}';
    }
}
