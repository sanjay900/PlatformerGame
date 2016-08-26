import com.sanjay900.ProcessingRunner;
import processing.core.PImage;

import java.awt.geom.Rectangle2D;

/**
 * Breakable is special, so it gets its own class
 */
public class Breakable extends Tile {
    Game game = (Game) ProcessingRunner.instance;
    boolean breaking = false;
    private long lastCount;
    Breakable(Rectangle2D.Float bounds) {
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

    public boolean breaking() {
        return breaking && game.frameCount - lastCount >= game.frameRate/2;
    }
}
