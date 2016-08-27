package game;

import MD2.Animation;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by sanjay on 27/08/2016.
 */
@AllArgsConstructor
@Getter
public enum AnimationCycles {
    WALKING(new Animation(0,8,0,1,1,0.5f)),
    JUMP(new Animation(11,12,0,1,1,0.3f));
    Animation animation;
}
