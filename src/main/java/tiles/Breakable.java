package tiles;

import com.sanjay900.ProcessingRunner;
import game.Game;

import java.awt.geom.Rectangle2D;

/**
 * tiles.Breakable is special, so it gets its own class
 */
public class Breakable extends Tile {
    Game game = (Game) ProcessingRunner.instance;
    public boolean breaking = false;
    private long lastCount;
    public Breakable(Rectangle2D.Float bounds) {
        this.type = TileType.BREAKABLE;
        this.bounds = bounds;
    }

    public void reset() {
        breaking = false;
    }

    public void startBreak() {
        breaking = true;
        lastCount = game.frameCount;
    }

    public boolean broken() {
        return breaking && game.frameCount - lastCount >= game.frameRate/2;
    }
}