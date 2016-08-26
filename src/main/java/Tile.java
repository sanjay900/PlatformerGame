import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Rectangle2D;

@Getter
@Setter
@AllArgsConstructor
public class Tile {
    Rectangle2D.Float bounds;
    TileType type;
    Tile() {}
}
