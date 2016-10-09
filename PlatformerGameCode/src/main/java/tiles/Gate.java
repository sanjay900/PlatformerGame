package tiles;

import game.Game;
import game.Player;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 8/10/2016.
 */
@Getter
public class Gate extends Tile {
    private boolean disabled = false;
    public Gate(Rectangle2D bounds, TileType type) {
        super(bounds, type);
    }
    public void reset() {
        disabled = false;
    }
    public void toggle(Game game) {
        if (game.current.buttons.stream().noneMatch(bt -> bt.colour().equals(colour()) && bt.isDown())) {
            reset();
        } else {
            disabled = true;
        }
    }
    public boolean collide(Tile pl) {
        return !disabled;
    }
    public String colour() {
        return type.name().replace("GATE_","").replace("_ON","").replace("_OFF","");
    }
}
