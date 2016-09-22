package levels;

import game.Game;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PImage;
import processing.core.PVector;
import tiles.*;

import java.awt.*;
import java.io.File;

public class LevelParser {

    public static Map parseLevel(Game game, File f) {
        Map map = new Map(game);
        PImage current = game.applet.loadImage(f.toString());
        current.loadPixels();
        if(!f.exists()) return null;
        map.platforms = new Tile[current.width][current.height];
        float squareHeight = game.applet.height / 24;
        float squareWidth = game.applet.width / 32;
        for (int x = 0; x < current.width; x++) {
            for (int y = 0; y < current.height; y++) {
                Rectangle2D bounds = new Rectangle2D(x * squareWidth, y * squareHeight, squareWidth, squareHeight);
                int c = current.pixels[y*current.width+x];
                if(c == Color.WHITE.getRGB()) continue;
                if(c == Color.GREEN.getRGB()) {
                    game.player.position = new PVector(x * squareWidth, y * squareHeight);
                    map.playerStart = new Tile(bounds, null);
                    continue;

                }
                if(c == Color.GRAY.getRGB()) {
                    Breakable breakable = new Breakable(bounds,game.applet.frameRate/3);
                    map.platforms[x][y] = breakable;
                    map.breakables.add(breakable);
                    continue;
                }
                if(c == TileType.BREAKFAST.color) {
                    Breakable breakable = new Breakable(bounds,game.applet.frameRate/4);
                    map.platforms[x][y] = breakable;
                    map.breakables.add(breakable);
                    continue;
                }
                if (c == TileType.COIN.color) {
                    Coin key = new Coin(bounds);
                    map.platforms[x][y] = key;
                    continue;
                }
                if (c == TileType.SPIKE.color) {
                    Tile tile = new Tile(new Rectangle2D(bounds.getX()+2,bounds.getY()+2, bounds.getWidth() -4, bounds.getHeight() -4),TileType.SPIKE);
                    map.platforms[x][y] = tile;
                    continue;
                }
                if (c == TileType.UPSIDE_DOWN_SPIKE.color) {
                    Tile tile = new Tile(bounds,TileType.UPSIDE_DOWN_SPIKE);
                    map.platforms[x][y] = tile;
                    continue;
                }
                if (c == TileType.KEY.color) {
                    Key key = new Key(bounds);
                    map.platforms[x][y] = key;
                    map.keys.add(key);
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

        return map;
    }
}
