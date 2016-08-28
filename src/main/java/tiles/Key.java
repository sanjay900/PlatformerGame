package tiles;

import java.awt.geom.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */

public class Key extends Tile {
    public float lastAngle = 0;
    public boolean gotten = false;
    Rectangle2D.Float origin;
    public boolean invisible = false;

    public Key(Rectangle2D.Float bounds) {
        super(bounds,TileType.KEY);
        origin = new Rectangle2D.Float(bounds.x,bounds.y,bounds.width,bounds.height);
    }
    public void reset() {
        gotten = false;
        invisible = false;
        bounds = new Rectangle2D.Float(origin.x,origin.y,origin.width,origin.height);
    }
}
