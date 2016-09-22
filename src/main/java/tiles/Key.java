package tiles;

import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */

public class Key extends Tile {
    public float lastAngle = 0;
    public boolean gotten = false;
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
}
