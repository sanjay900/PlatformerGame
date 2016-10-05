package tiles;


import game.Player;
import net.tangentmc.collisions.Rectangle2D;

public class Coin extends Tile {
    public float lastAngle = 0;
    public Coin(Rectangle2D rect) {
        super(rect,TileType.COIN);
    }
    public boolean gotten = false;
    public void reset() {
        gotten = false;
    }
    public boolean collide(Player pl) {
        if (!gotten) {
            pl.getGame().getSoundResolver().accept("COIN.wav");
            pl.getGame().coins++;
        }
        gotten = true;
        return false;
    }
}
