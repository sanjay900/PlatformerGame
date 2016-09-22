package tiles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tangentmc.collisions.Rectangle2D;

@Getter
@Setter
@AllArgsConstructor
public class Tile {
    public Rectangle2D bounds;
    public TileType type;
}
