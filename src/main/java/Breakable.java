import com.sanjay900.ProcessingRunner;
import processing.core.PImage;

import java.awt.geom.Rectangle2D;

/**
 * Breakable is special, so it gets its own class
 */
public class Breakable extends Tile {
    Game game = (Game) ProcessingRunner.instance;
    PImage image;
    boolean breaking = false;
    private long lastCount;
    Breakable(Rectangle2D.Float bounds) {
        this.bounds = bounds;
        image = game.loadImage(TileType.BLOCK.fileName);
    }

    public void reset() {
        image = game.loadImage(TileType.BLOCK.fileName);
        breaking = false;
    }

    public void startBreak() {
        breaking = true;
        image = game.loadImage(TileType.BREAKABLE.fileName);
        lastCount = game.frameCount;
    }

    public boolean breaking() {
        return breaking && game.frameCount - lastCount >= game.frameRate/2;
    }
}
