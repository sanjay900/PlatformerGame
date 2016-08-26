import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LevelParser {
    public static Map parseLevel(Game game, int levelNum) {
        Map map = new Map(game);
        BufferedImage current;
        try {
            current = ImageIO.read(new File("levels/level" + levelNum + ".png"));
            map.platforms = new Tile[current.getWidth()][current.getHeight()];
            float squareHeight = game.height / 24;
            float squareWidth = game.width / 32;
            for (int x = 0; x < current.getWidth(); x++) {
                for (int y = 0; y < current.getHeight(); y++) {
                    Rectangle2D.Float bounds = new Rectangle2D.Float((x) * squareWidth, y * squareHeight, squareWidth, squareHeight);
                    int c = current.getRGB(x,y);
                    if(c == Color.WHITE.getRGB()) continue;
                    if(c == Color.GREEN.getRGB()) {
                        game.player = new Player((x) * squareWidth, y * squareHeight,game);
                        continue;
                    }
                    for (TileType tileType : TileType.values()) {
                        if(c == tileType.color) {
                            System.out.println(tileType.name());
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
