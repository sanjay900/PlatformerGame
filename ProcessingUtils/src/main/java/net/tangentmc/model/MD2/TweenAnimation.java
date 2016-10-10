package net.tangentmc.model.MD2;

/**
 * Created by sanjay on 25/08/2016.
 * An animation that interpolates between two different animations
 */
class TweenAnimation extends Animation {
    private Animation next;
    private boolean isIntermediate = false;
    public TweenAnimation(Animation current, Animation next, float transitionSpeed) {
        super(1, current.getCurrframe(), current.getScale(), transitionSpeed);
        this.next = next;
    }
    @Override
    void nextFrame() {
        intorpol += speed;
        if (intorpol >= 1) {
            isIntermediate = true;
        }
    }


    boolean isIntermediate() {
        return isIntermediate;
    }
    @Override
    int getNextFrame() {
        return next.getCurrframe();
    }

    Animation getNext() {
        return next;
    }
}
