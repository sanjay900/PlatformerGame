import java.awt.geom.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */
public class Coin extends Tile {
    float lastAngle = 0;
    public Coin(Rectangle2D.Float rect) {
        super(rect,TileType.COIN);
    }
    boolean gotten = false;
    public void reset() {
        gotten = false;
    }
}
