import java.awt.geom.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */
public class Key extends Tile {
    boolean gotten = false;
    public boolean invisible = false;

    public Key(Rectangle2D.Float bounds) {
        super(bounds,TileType.KEY);
    }
}
