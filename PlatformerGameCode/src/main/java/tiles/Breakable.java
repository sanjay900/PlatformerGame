package tiles;


import game.Game;
import game.Player;
import net.tangentmc.collisions.Rectangle2D;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;


/**
 * tiles.Breakable is special, so it gets its own class
 */
public class Breakable extends Tile {
    public boolean breaking = false;
    private long lastCount;
    float delay;
    Game game;
    public Breakable(Rectangle2D bounds, float delay, Game game) {
        super(bounds,TileType.BREAKABLE);
        this.delay = delay;
        this.game = game;
    }

    public void reset() {
        breaking = false;
    }

    public void startBreak() {
        breaking = true;
        lastCount = game.applet.frameCount;
    }

    public boolean broken() {
        return breaking && game.applet.frameCount - lastCount >= delay;
    }

    @Override
    public boolean collide(Tile pl) {
        if (broken()) return false;
        if (breaking) return true;
        startBreak();
        game.getSoundResolver().accept(false,"bREAK.WAV");
        return true;
    }
}
