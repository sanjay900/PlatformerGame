package levels;

import game.Game;
import game.Player;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PImage;
import tiles.*;
import tiles.Button;

import java.awt.*;
import java.io.File;

public class LevelParser {

    public static Map parseLevel(Game game, File f) {
        Map map = new Map(game);
        PImage current = game.applet.loadImage(f.toString());
        current.loadPixels();
        if(!f.exists()) return null;
        map.xLength = current.width;
        map.yLength = current.height;
        float squareSize = game.applet.height / 24;
        game.players.clear();
        for (int x = 0; x < current.width; x++) {
            for (int y = 0; y < current.height; y++) {
                Rectangle2D bounds = new Rectangle2D(x * squareSize, y * squareSize, squareSize, squareSize);
                int c = current.pixels[y*current.width+x];
                if(c == Color.WHITE.getRGB()) continue;
                if(c == Color.GREEN.getRGB()) {
                    game.players.add(new Player(x * squareSize, y * squareSize,game));
                    map.playerStart = new Tile(bounds, null);
                    continue;

                }
                if(c == Color.GRAY.getRGB()) {
                    Breakable breakable = new Breakable(bounds,game.applet.frameRate/3, game);
                    map.platforms.add(breakable);
                    map.breakables.add(breakable);
                    continue;
                }
                if(c == TileType.BREAKFAST.color) {
                    Breakable breakable = new Breakable(bounds,game.applet.frameRate/4, game);
                    map.platforms.add(breakable);
                    map.breakables.add(breakable);
                    continue;
                }
                if (c == TileType.COIN.color) {
                    Coin key = new Coin(bounds);
                    map.platforms.add(key);
                    continue;
                }
                if (c == TileType.SPIKE.color) {
                    Tile tile = new Tile(new Rectangle2D(bounds.getX()+2,bounds.getY()+2, bounds.getWidth() -4, bounds.getHeight() -4),TileType.SPIKE);
                    map.platforms.add(tile);
                    continue;
                }
                if (c == TileType.UPSIDE_DOWN_SPIKE.color) {
                    Tile tile = new Tile(bounds,TileType.UPSIDE_DOWN_SPIKE);
                    map.platforms.add(tile);
                    continue;
                }
                if (c == TileType.KEY.color) {
                    Key key = new Key(bounds);
                    map.platforms.add(key);
                    map.keys.add(key);
                    continue;
                }
                if (c == TileType.BOX.color) {
                    //Boxes can be pushed, so working from the tile array would get tedious.
                    map.boxs.add(new Box(bounds,game));
                    continue;
                }
                for (TileType tileType : TileType.values()) {
                    if(c == tileType.color) {
                        if (tileType.name().contains("TELEPORTER")) {
                            Teleporter tele = new Teleporter(bounds,tileType,game);
                            map.platforms.add(tele);
                            map.teleporters.add(tele);
                        } else if (tileType.name().contains("GATE")) {
                            Gate gate = new Gate(bounds, tileType);
                            map.platforms.add(gate);
                            map.gates.add(gate);
                        } else if (tileType.name().contains("BUTTON")) {
                            Button bt = new Button(bounds,tileType,game);
                            map.buttons.add(bt);
                            map.platforms.add(bt);
                        } else {
                            map.platforms.add(new Tile(bounds, tileType));
                        }
                        break;
                    }
                }
            }
        }
        game.currentPlayer = game.players.get(0);

        return map;
    }
}
