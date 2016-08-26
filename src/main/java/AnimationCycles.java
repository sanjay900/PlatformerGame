import MD2.Animation;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by sanjay on 27/08/2016.
 */
@AllArgsConstructor
@Getter
public enum AnimationCycles {
    WALKING(new Animation(0,8,0,0,1,1)),
    JUMP(new Animation(9,13,0,0,1,1));
    Animation animation;
}
