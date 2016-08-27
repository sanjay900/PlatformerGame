package levels;

import game.Game;
import tiles.*;
import processing.core.PVector;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.abs;
import static processing.core.PConstants.*;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Map {
    public static int levelNum = 0;
    Game game;
    public Tile[][] platforms;
    public Tile playerStart;
    public List<Breakable> breakables = new ArrayList<>();
    public List<Key> keys = new ArrayList<>();
    public Map(Game game) {
        this.game = game;
        breakables = new ArrayList<>();
    }
    public void drawKeys() {
        float tileWidth = (float) playerStart.bounds.getWidth();
        float tileHeight = (float) playerStart.bounds.getHeight();
        for (int i = 0; i < keys.size(); i++) {
            Key tile = keys.get(i);
            game.pushMatrix();
            float dx = 32-((i+1)*4)-0.5f;
            float dy = 2;
            game.translate(dx*(float)tile.getBounds().getWidth(), dy*(float)tile.getBounds().getHeight(), 0);
            game.scale((float)tile.getBounds().getWidth(),(float)tile.getBounds().getHeight(),(float)tile.getBounds().getWidth());
            game.rotate(HALF_PI,1,0,0);
            game.rotate(HALF_PI,0,0,1);
            game.rotate(PI,0,0,1);
            if (tile.invisible) {
                TileType.KEY_SLOT_FILLED.model.drawModel();
            } else {
                TileType.KEY_SLOT.model.drawModel();
            }
            game.popMatrix();
        }
    }
    public void drawFrame() {
        float tileWidth = (float) playerStart.bounds.getWidth();
        float tileHeight = (float) playerStart.bounds.getHeight();
        for (int i = 0; i < keys.size(); i++) {
            Key tile = keys.get(i);
            if (tile.gotten) {
                PVector to = new PVector((float)(game.player.getBounds().getX()+400) - ((i + 1) * tileWidth * 4), tileHeight);
                PVector from = new PVector(tile.bounds.x, tile.bounds.y);
                if (abs(from.dist(to)) < 11) {
                    tile.invisible = true;
                }
                PVector velocity = to.sub(from).normalize().mult(40);
                PVector dest = from.add(velocity);
                tile.setBounds(new Rectangle2D.Float(dest.x, dest.y, tileWidth, tileHeight));
            }
            game.pushMatrix();
            game.translate(tile.getBounds().x + (float) tile.getBounds().getWidth() / 2, tile.getBounds().y + (float) tile.getBounds().getHeight(), 0);
            game.scale((float) tile.getBounds().getWidth(), (float) tile.getBounds().getHeight(), (float) tile.getBounds().getWidth());
            game.rotate(HALF_PI, 1, 0, 0);
            game.rotate(HALF_PI, 0, 0, 1);
            if (!tile.invisible)
                tile.type.model.drawModel();
            game.popMatrix();
        }
        Tile tile;
        for (Tile[] platform : platforms) {
            for (Tile aPlatform : platform) {
                if ((tile = aPlatform) == null) continue;
                if (tile instanceof Key && ((Key) tile).gotten) {
                    continue;
                }
                if (tile instanceof Coin && ((Coin) tile).gotten) {
                    continue;
                }
                game.pushMatrix();
                game.translate(tile.getBounds().x+(float)tile.getBounds().getWidth()/2, tile.getBounds().y+(float)tile.getBounds().getHeight(), 0);

                if (tile.type.model != null) {
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE) {
                        game.translate(0, -(float) tile.getBounds().getHeight(), 0);
                    }
                    if (tile.type == TileType.EXIT) {
                        game.translate(0, -(float) tile.getBounds().getHeight(), 0);
                    }
                }
                if (tile instanceof Coin) {
                    game.scale(0.75f);
                }
                game.scale((float)tile.getBounds().getWidth(),(float)tile.getBounds().getHeight(),(float)tile.getBounds().getWidth());
                game.rotate(HALF_PI,1,0,0);
                game.rotate(HALF_PI,0,0,1);
                if (tile.type.model != null) {

                    if (tile instanceof Coin) {
                        game.rotate(((Coin) tile).lastAngle+=game.random(0.1f,0.5f));
                        if (((Coin) tile).lastAngle >= TWO_PI) ((Coin) tile).lastAngle = 0;
                    }
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
                        if (tile.type == TileType.EXIT) {
                            game.rotate(game.random(0,TWO_PI),game.random(0,1),game.random(0,1),game.random(0,1));
                        }
                        tile.type.model.drawModel();
                    }
                }
                game.popMatrix();
            }
        }
    }
}
