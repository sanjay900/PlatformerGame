package net.tangentmc.model.MD2;

/**
 * Store the required variables for creating an animation based on a model
 * This also takes care of scaling the models and what speed they animate at.
 */
public class Animation {
    public Animation(int framecount, int startFrame, float scale, float speed) {
        this.framecount = framecount;
        this.startFrame = startFrame;
        this.endFrame = startFrame+framecount-1;
        this.currframe = startFrame;
        this.scale = scale;
        this.speed = speed;
    }
    private int framecount;
    private int startFrame;
    private int endFrame;
    private int currframe;
    float intorpol = 0;
    private float scale;
    float speed = 1;
    float realSpeed;
    @java.beans.ConstructorProperties({"isIntermediate", "framecount", "startFrame", "endFrame", "currframe", "intorpol", "scale", "speed"})
    public Animation(int startFrame, int endFrame, int currframe, float intorpol, float scale, float speed) {
        this.framecount = endFrame-startFrame;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.currframe = currframe;
        this.intorpol = intorpol;
        this.scale = scale;
        this.speed = realSpeed = speed;
    }
    void stop() {
        speed = 0;
    }
    void resume() {
        speed = realSpeed;
    }
    void nextFrame() {
        intorpol += speed;
        if (intorpol >= 1) {
            intorpol = 0;
            currframe = getNextFrame();
        }
    }
    int getNextFrame() {
        if (currframe+1 > endFrame) return startFrame;
        return currframe+1;
    }

    public static Animation fromAnimation(Animation animation) {
        return new Animation(animation.startFrame,animation.endFrame,animation.currframe,animation.intorpol,animation.scale,animation.speed);
    }

    public int getFramecount() {
        return framecount;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public int getCurrframe() {
        return currframe;
    }

    public float getIntorpol() {
        return intorpol;
    }

    public float getScale() {
        return scale;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Animation{"+
                ", framecount=" + framecount +
                ", startFrame=" + startFrame +
                ", endFrame=" + endFrame +
                ", currframe=" + currframe +
                ", intorpol=" + intorpol +
                ", scale=" + scale +
                ", speed=" + speed +
                '}';
    }
}
