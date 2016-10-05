package tiles;


import game.Player;
import net.tangentmc.collisions.Rectangle2D;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;


/**
 * tiles.Breakable is special, so it gets its own class
 */
public class Breakable extends Tile {
    PApplet game = (PApplet) ProcessingRunner.instance;
    public boolean breaking = false;
    private long lastCount;
    float delay;
    public Breakable(Rectangle2D bounds, float delay) {
        super(bounds,TileType.BREAKABLE);
        this.delay = delay;
    }

    public void reset() {
        breaking = false;
    }

    public void startBreak() {
        breaking = true;
        lastCount = game.frameCount;
    }

    public boolean broken() {
        return breaking && game.frameCount - lastCount >= delay;
    }

    @Override
    public boolean collide(Player pl) {
        if (broken()) return false;
        if (breaking) return true;
        startBreak();
        pl.getGame().getSoundResolver().accept("bREAK.WAV");
        return true;
    }
}
