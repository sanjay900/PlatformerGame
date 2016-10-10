package tiles;

import game.Game;
import game.Player;
import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 9/10/2016.
 */
public class Teleporter extends Tile {
    private static Game game;
    public Teleporter link;
    public Teleporter(Rectangle2D bounds, TileType type, Game game) {
        super(bounds, type);
        Teleporter.game = game;
    }
    public boolean collide(Tile pl) {
        Teleporter linked = getLink();
        if (linked == null) return true;
        if (pl instanceof Player) {
            ((Player)pl).teleport(linked);
        }
        if (pl instanceof Box) {
            return ((Box) pl).teleport(linked);
        }
        return true;
    }

    public Teleporter getLink() {
        return link;
    }
}
