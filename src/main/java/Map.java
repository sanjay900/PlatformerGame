/**
 * Created by sanjay on 26/08/2016.
 */
public class Map {
    static int levelNum = 1;
    Game game;
    Tile[][] platforms;
    Tile playerStart;
    public Map(Game game) {
        this.game = game;
    }
    public void drawFrame() {
        Tile tile;
        for (Tile[] platform : platforms) {
            for (Tile aPlatform : platform) {
                if ((tile = aPlatform) == null) continue;
                game.image(tile.type.image, tile.getBounds().x, tile.getBounds().y, tile.getBounds().width, tile.getBounds().height);
            }
        }
    }
}
