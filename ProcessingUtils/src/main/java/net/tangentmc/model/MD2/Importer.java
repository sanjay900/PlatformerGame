package net.tangentmc.model.MD2;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

/**
 * A MD2 Model importer
 * Imports MD2 Models in a format that processing understands.
 */
public class Importer {
    public MD2Model importModel(File f, PImage texture, PApplet applet) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(Files.readAllBytes(f.toPath()));
        //MD2 happens to be in the LITTLE_ENDIAN format, so lets fix that.
        buf.order(ByteOrder.LITTLE_ENDIAN);
        Header header = new Header(buf);
        //The below three are unused in favour of using textures from processing
        //and using gl commands
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        String[] skins = new String[header.getSkinCount()];
        short[][] uvs = new short[header.getUVCount()][2];
        short[][] tris = new short[header.getTriCount()][6];
        Frame[] frames = new Frame[header.getFrameCount()];
        float[] glCmds = new float[header.getGlCmdCount()];
        buf.position(header.getSkinOffset());
        byte[] skin = new byte[64];
        for (int i = 0; i < header.getSkinCount(); i++) {
            buf.get(skin);
           skins[i] = new String(skin).trim();
        }
        buf.position(header.getUVOffset());
        for (int i = 0; i < header.getUVCount(); i++) {
            uvs[i][0] = buf.getShort();
            uvs[i][1] = buf.getShort();
        }
        buf.position(header.getTriOffset());
        for (int i = 0; i < header.getTriCount(); i++) {
            for (int i2 = 0; i2 < tris[i].length; i2++) {
                tris[i][i2] = buf.getShort();
            }
        }
        buf.position(header.getFrameOffset());
        for (int i = 0; i < header.getFrameCount(); i++) {
            frames[i] = new Frame(buf, header.getVertexCount());
        }
        buf.position(header.getGlCmdOffset());
        int index;
        for (int i = 0; i < header.getGlCmdCount();) {
            glCmds[i++] = index = buf.getInt();
            for (int i2 = 0; i2<Math.abs(index);i2++) {
                glCmds[i++] = buf.getFloat();
                glCmds[i++] = buf.getFloat();
                glCmds[i++] = buf.getInt();
            }
        }
        return new MD2Model(header,frames,glCmds,texture,applet,uvs,tris);
    }

}
