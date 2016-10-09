package tiles;

import game.Game;
import game.Player;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PVector;

/**
 * Created by sanjay on 9/10/2016.
 */
@Getter
public class Box extends Tile {
    private static Game game;
    private PVector position;
    private PVector origPosition;
    private PVector velocity = new PVector();
    PVector gravity = new PVector(0,20/30f);
    public Box(Rectangle2D bounds, Game game) {
        super(bounds, TileType.BOX);
        Box.game = game;
        position = new PVector(bounds.getX(),bounds.getY());
        origPosition = position.copy();
    }
    double lastX = 0;
    @Override
    public boolean collide(Tile pl) {
        PVector vel = null;
        if (pl instanceof Player) vel = ((Player) pl).getVelocity();
        if (vel == null) return true;
        lastX = vel.x;
        position.add(vel.x*2,0);
        if (collides(pl)) {
            position.sub(vel.x*2,0);
            return true;
        }

        return false;
    }
    public void tick() {
        velocity.add(gravity);
        position.add(velocity);
        if (lastTp != null && !lastTp.getLink().getBounds().intersects(getBounds()) && !lastTp.getBounds().intersects(getBounds())) {
            lastTp = null;
        }
        if (collides(null)) {
            position.sub(velocity);
            velocity = new PVector();
        }
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D(position.x,position.y,bounds.getWidth(),bounds.getHeight());
    }
    private boolean collides(Tile pl) {
        for (Tile tile:game.current.platforms) {
            if (tile != null && tile != pl && tile.getBounds().intersects(getBounds())) {
                if (tile.collide(this))
                    return true;
            }
        }
        for (Player player : game.players) {
            if (player != pl && player.getBounds().intersects(getBounds())) {
                return true;
            }
        }
        for (Box box : game.current.boxs) {
            if (box != this && box != pl && box.getBounds().intersects(getBounds())) {
                return true;
            }
        }
        return false;
    }
    Teleporter lastTp = null;
    public boolean teleport(Teleporter dest) {
        if (lastTp == dest || lastTp == dest.getLink()) return false;
        PVector old = position;
        this.position = new PVector(dest.getBounds().getX(),dest.getBounds().getY()-80);
        if (lastX > 0) {
            position.add(getBounds().getWidth(),0);
        } else if (lastX < 0) {
            position.add(-getBounds().getWidth(),0);
        }
        if (collides(dest)) {
            position = old;
            return false;
        }
        lastTp = dest;

        return true;
    }

    public void reset() {
        position = origPosition.copy();
    }
}
