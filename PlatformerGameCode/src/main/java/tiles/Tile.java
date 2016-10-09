package tiles;

import game.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tangentmc.collisions.Rectangle2D;

@Getter
@Setter
@AllArgsConstructor
public class Tile {
    protected Rectangle2D bounds;
    public TileType type;

    /**
     * Collision events with player
     * @param pl the player
     * @return true if this object is solid, false otherwise
     */
    public boolean collide(Tile pl) {
        return true;
    }
}
