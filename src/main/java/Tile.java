import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by sanjay on 26/08/2016.
 */
@Getter
@Setter
@AllArgsConstructor
public class Tile {
    Rectangle2D.Float bounds;
    Color color;
}
