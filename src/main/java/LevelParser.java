import processing.core.PVector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LevelParser {
    public static Map parseLevel(Game game, int levelNum) {
        ArrayList<Key> keys = new ArrayList<>();
        Map map = new Map(game);
        BufferedImage current;
        try {
            File f = new File("levels/level" + levelNum + ".png");
            if(!f.exists()) return null;
            current = ImageIO.read(f);
            map.platforms = new Tile[current.getWidth()][current.getHeight()];
            float squareHeight = game.height / 24;
            float squareWidth = game.width / 32;
            for (int x = 0; x < current.getWidth(); x++) {
                for (int y = 0; y < current.getHeight(); y++) {
                    Rectangle2D.Float bounds = new Rectangle2D.Float(x * squareWidth, y * squareHeight, squareWidth, squareHeight);
                    int c = current.getRGB(x,y);
                    if(c == Color.WHITE.getRGB()) continue;
                    if(c == Color.GREEN.getRGB()) {
                        game.player.position = new PVector(x * squareWidth, y * squareHeight);
                        map.playerStart = new Tile(bounds, null);
                        continue;
                    }
                    if(c == Color.GRAY.getRGB()) {
                        Breakable breakable = new Breakable(bounds);
                        map.platforms[x][y] = breakable;
                        map.breakables.add(breakable);
                        continue;
                    }
                    if (c == TileType.KEY.color) {
                        Key key = new Key(bounds);
                        map.keys.add(key);
                        map.platforms[x][y] = key;
                        continue;
                    }
                    for (TileType tileType : TileType.values()) {
                        if(c == tileType.color) {
                            map.platforms[x][y] = new Tile(bounds, tileType);
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
