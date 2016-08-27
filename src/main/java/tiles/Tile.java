package tiles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Rectangle2D;

@Getter
@Setter
@AllArgsConstructor
public class Tile {
    public Rectangle2D.Float bounds;
    public TileType type;
    Tile() {}
}
