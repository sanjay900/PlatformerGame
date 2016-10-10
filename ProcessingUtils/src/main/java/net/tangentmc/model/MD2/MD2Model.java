package net.tangentmc.model.MD2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PConstants.NORMAL;

/**
 * Animated model support for processing.
 * based upon the MD2 File format from idSoftware.
 * more information: http://tfc.duke.free.fr/coding/md2-specs-en.html
 */

public class MD2Model {
    private Header header;
    public Frame[] frames;
    private float[] glcmds;
    private PImage texture;
    private PApplet applet;
    short[][] uvs;
    short[][] tris;
    private Animation animation = new Animation(1,0,1,1);

    MD2Model(Header header, Frame[] frames, float[] glCmds, PImage texture, PApplet applet, short[][] uvs, short[][] tris) {
        this.header = header;
        this.frames = frames;
        this.glcmds = glCmds;
        this.texture = texture;
        this.applet = applet;
        this.uvs = uvs;
        this.tris = tris;
    }

    public void setAnimation(Animation animation, float transitionSpeed) {
        if (this.animation == null || transitionSpeed >= 1) {
            this.animation = animation;
            return;
        }
        this.animation = new TweenAnimation(this.animation, Animation.fromAnimation(animation),transitionSpeed);
    }

    /**
     * If your frames are named based on their animations, this will split prefixed frames into
     * an array of animations. This can be helpful for working out frames.
     * @return an array of generated animations
     */
    public Animation[] getPregeneratedAnimations() {
        List<Animation> animationList = new ArrayList<>();
        String lastPrefix = frames[0].getName().replaceAll("[^\\D.]", "").replace("\0","");
        String prefix;
        int firstFrame = 0;
        int lastFrame = 0;
        for (int i = 0; i < frames.length; i++) {
            prefix = frames[i].getName().replaceAll("[^\\D.]", "").replace("\0","");
            if (prefix.equals(lastPrefix)) {
                lastFrame = i;
            } else {
                animationList.add(new Animation(firstFrame,lastFrame,0,0,1,1));
                firstFrame = lastFrame = i;
            }
        }
        animationList.add(new Animation(firstFrame,lastFrame,0,0,1,1));
        return animationList.toArray(new Animation[animationList.size()]);
    }
    public void drawModel() {
        if (animation instanceof TweenAnimation && ((TweenAnimation) animation).isIntermediate()) {
            animation = ((TweenAnimation) animation).getNext();
        }
        applet.textureMode(NORMAL);
        applet.pushMatrix();
        renderFrame();
        applet.popMatrix();
        animation.nextFrame();
    }
    private void renderFrame() {
        Vertex[] vertlist = interpolate();
        if (glcmds.length == 0) {
            for (short[] tri:tris) {
                applet.beginShape(PConstants.TRIANGLE);
                applet.texture(texture);
                float u,v;
                int index;
                float[] normals;
                for (int i =0; i < 3; i++) {
                    u = uvs[tri[3+i]][0] / (float) header.getSkinwidth();
                    v = uvs[tri[3+i]][1] / (float) header.getSkinheight();
                    index = tri[i];
                    applet.vertex(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z, u, v);
                    normals = NormalTable.normalTable[vertlist[index].getLightnormalindex()];
                    applet.normal(normals[0], normals[1], normals[2]);
                }
                applet.endShape();
            }

        } else {
            for (int cmdI = 0; cmdI < glcmds.length; ) {
                int cmd = (int) glcmds[cmdI++];
                if (cmd < 0) {
                    cmd = -cmd;
                    applet.beginShape(PConstants.TRIANGLE_FAN);
                } else {
                    applet.beginShape(PConstants.TRIANGLE_STRIP);
                }
                applet.texture(texture);
                float u, v;
                int index;
                float[] normals;
                for (int i = 0; i < cmd; i++) {
                    u = glcmds[cmdI++];
                    v = glcmds[cmdI++];
                    index = (int) glcmds[cmdI++];
                    applet.vertex(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z, u, v);
                    normals = NormalTable.normalTable[vertlist[index].getLightnormalindex()];
                    applet.normal(normals[0], normals[1], normals[2]);
                }
                applet.endShape();
            }
        }
    }
    public void stopAnimation() {
        animation.stop();
    }
    public void startAnimation() {
        animation.resume();
    }
    private Vertex[] interpolate() {
        Vertex[] vertlist = new Vertex[header.getVertexCount()];
        Vertex[] current = frames[animation.getCurrframe()].getVerticies();
        Vertex[] next = frames[animation.getNextFrame()].getVerticies();
        for (int i = 0; i< header.getVertexCount(); i++) {
            vertlist[i] = new Vertex(new PVector((current[i].getVert().x+ animation.getIntorpol() *(next[i].getVert().x-current[i].getVert().x))* animation.getScale(),
                    (current[i].getVert().y+animation.getIntorpol()*(next[i].getVert().y-current[i].getVert().y))*animation.getScale(),
                    (current[i].getVert().z+animation.getIntorpol()*(next[i].getVert().z-current[i].getVert().z))*animation.getScale()),current[i].getLightnormalindex());
        }
        return vertlist;
    }
}
