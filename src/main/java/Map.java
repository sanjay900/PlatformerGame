import java.util.ArrayList;
import java.util.List;

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
                if (tile instanceof Breakable) {
                    Breakable breakTile = (Breakable) tile;
                    if (breakTile.breaking()) continue;
                    game.image(breakTile.image, breakTile.bounds.x, breakTile.bounds.y, breakTile.bounds.width, breakTile.bounds.height);
                    continue;
                }
                game.image(tile.type.image, tile.bounds.x, tile.bounds.y, tile.bounds.width, tile.bounds.height);
            }
        }
    }
}
