import java.util.ArrayList;
import java.util.List;

import static processing.core.PConstants.*;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Map {
    static int levelNum = 1;
    Game game;
    Tile[][] platforms;
    Tile playerStart;
    static List<Breakable> breakables = new ArrayList<>();
    public Map(Game game) {
        this.game = game;
        breakables = new ArrayList<>();
    }
    public void drawFrame() {
        Tile tile;
        for (Tile[] platform : platforms) {
            for (Tile aPlatform : platform) {
                if ((tile = aPlatform) == null) continue;
                game.pushMatrix();
                game.translate(tile.getBounds().x+(float)tile.getBounds().getWidth()/2, tile.getBounds().y+(float)tile.getBounds().getHeight(), 0);

                if (tile.type.model != null) {
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE) {
                        game.translate(0, -(float) tile.getBounds().getHeight(), 0);
                    }
                }
                game.scale((float)tile.getBounds().getWidth(),(float)tile.getBounds().getHeight(),(float)tile.getBounds().getWidth());
                game.rotate(HALF_PI,1,0,0);
                game.rotate(HALF_PI,0,0,1);
                if (tile.type.model != null) {
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE) {
                        game.rotate(PI,0,1,0);
                    }

                    if (tile instanceof Breakable) {
                        Breakable breakTile = (Breakable) tile;
                        if (!breakTile.broken()) {
                            if (breakTile.breaking) TileType.BREAKABLE.model.drawModel();
                            else TileType.BLOCK.model.drawModel();
                        }
                    } else {
                        tile.type.model.drawModel();
                    }
                }
                game.popMatrix();
            }
        }
    }
}
