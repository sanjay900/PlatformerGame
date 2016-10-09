package tiles;

import game.Player;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */

public class Key extends Tile {
    public float lastAngle = 0;
    @Getter
    private boolean gotten = false;
    Rectangle2D origin;
    public boolean invisible = false;

    public Key(Rectangle2D bounds) {
        super(bounds,TileType.KEY);
        origin = new Rectangle2D(bounds);
    }
    public void reset() {
        gotten = false;
        invisible = false;
        bounds = new Rectangle2D(origin);
    }
    @Override
    public boolean collide(Tile pl) {
        if (!(pl instanceof Player)) return false;
        if (!gotten) ((Player)pl).getGame().getSoundResolver().accept(false,"KEY.wav");
        gotten = true;
        return false;
    }
}
