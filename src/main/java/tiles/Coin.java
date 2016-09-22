package tiles;


import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 27/08/2016.
 */
public class Coin extends Tile {
    public float lastAngle = 0;
    public Coin(Rectangle2D rect) {
        super(rect,TileType.COIN);
    }
    public boolean gotten = false;
    public void reset() {
        gotten = false;
    }
}
