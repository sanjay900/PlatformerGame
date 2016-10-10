import net.tangentmc.model.MD2.Animation;
import lombok.Getter;

/**
 * Created by sanjay on 25/08/2016.
 */
@Getter
public enum StinkyAnimations {
    WALKING(new Animation(12,0,100,1f)),
    PUSHING(new Animation(10,22,100,1f));

    private final Animation animation;

    StinkyAnimations(Animation animation) {
        this.animation = animation;
    }
}
