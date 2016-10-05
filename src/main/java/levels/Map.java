package levels;

import game.Game;
import net.tangentmc.collisions.Rectangle2D;
import tiles.*;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.abs;
import static processing.core.PConstants.*;

public class Map {
    Game game;
    public Tile[][] platforms;
    public Tile playerStart;
    public List<Breakable> breakables = new ArrayList<>();
    public List<Key> keys = new ArrayList<>();
    float lastFlagAngle = 0;
    public Map(Game game) {
        this.game = game;
        breakables = new ArrayList<>();
    }
    public void drawKeys() {
        for (int i = 0; i < keys.size(); i++) {
            Key tile = keys.get(i);
            game.applet.pushMatrix();
            float dx = 32-((i+1)*4)-0.5f;
            float dy = 2;
            game.applet.translate(dx* tile.getBounds().getWidth(), dy* tile.getBounds().getHeight(), 0);
            game.applet.scale(tile.getBounds().getWidth(), tile.getBounds().getHeight(), tile.getBounds().getWidth());
            game.applet.rotate(HALF_PI,1,0,0);
            game.applet.rotate(HALF_PI,0,0,1);
            game.applet.rotate(PI,0,0,1);
            if (tile.invisible) {
                TileType.KEY_SLOT_FILLED.model.drawModel();
            } else {
                TileType.KEY_SLOT.model.drawModel();
            }
            game.applet.popMatrix();
        }
    }
    public void drawFrame() {
        float tileWidth = playerStart.bounds.getWidth();
        float tileHeight = playerStart.bounds.getHeight();
        for (int i = 0; i < keys.size(); i++) {
            Key tile = keys.get(i);
            if (tile.isGotten()) {
                PVector to = new PVector(game.currentPlayer.getBounds().getX()+400 - ((i + 1) * tileWidth * 4), tileHeight);
                PVector from = new PVector(tile.bounds.getX(), tile.bounds.getY());
                if (abs(from.dist(to)) < 15) {
                    tile.invisible = true;
                }
                PVector velocity = to.sub(from).normalize().mult(20);
                PVector dest = from.add(velocity);
                tile.setBounds(new Rectangle2D(dest.x, dest.y, tileWidth, tileHeight));
                game.applet.pushMatrix();
                game.applet.translate(tile.getBounds().getX() + tile.getBounds().getWidth() / 2, tile.getBounds().getY() + tile.getBounds().getHeight(), 0);
                game.applet.scale(tile.getBounds().getWidth(), tile.getBounds().getHeight(), tile.getBounds().getWidth());
                game.applet.rotate(HALF_PI, 1, 0, 0);
                game.applet.rotate(HALF_PI, 0, 0, 1);
                if (!tile.invisible)
                    tile.type.model.drawModel();
                game.applet.popMatrix();

            }
        }
        Tile tile;
        for (Tile[] platform : platforms) {
            for (Tile aPlatform : platform) {
                if ((tile = aPlatform) == null) continue;
                if (tile instanceof Key && ((Key) tile).isGotten()) {
                    continue;
                }
                if (tile instanceof Coin && ((Coin) tile).gotten) {
                    continue;
                }
                //Clip elements to the screen
                int xBuff = game.applet.width/2+50;
                if (tile.getBounds().getX() > xBuff+ Math.max(game.currentPlayer.getBounds().getX(),xBuff)) continue;
                if (tile.getBounds().getX() < Math.min(game.currentPlayer.getBounds().getX(),platforms.length * game.currentPlayer.getBounds().getWidth()-xBuff)-xBuff) continue;
                game.applet.pushMatrix();
                game.applet.translate(tile.getBounds().getX()+ tile.getBounds().getWidth() /2, tile.getBounds().getY()+ tile.getBounds().getHeight(), 0);

                if (tile.type.model != null) {
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE) {
                        game.applet.translate(0, -tile.getBounds().getHeight(), 0);
                    } else if (tile.type == TileType.LEFT_SPIKE) {
                        game.applet.rotate(HALF_PI, 0, 0, -1);
                        game.applet.translate(tile.getBounds().getHeight() / 2, tile.getBounds().getWidth() / 2, 0);
                    } else if (tile.type == TileType.RIGHT_SPIKE) {
                        game.applet.rotate(HALF_PI, 0, 0, 1);
                        game.applet.translate(-(tile.getBounds().getHeight() / 2), tile.getBounds().getWidth() / 2, 0);
                    }
                }
                if (tile instanceof Coin) {
                    game.applet.scale(0.75f);
                }
                game.applet.scale(tile.getBounds().getWidth(), tile.getBounds().getHeight(), tile.getBounds().getWidth());
                game.applet.rotate(HALF_PI,1,0,0);
                game.applet.rotate(HALF_PI,0,0,1);
                if (tile.type.model != null) {

                    if (tile instanceof Coin) {
                        game.applet.rotate(((Coin) tile).lastAngle+=0.1f);
                        if (((Coin) tile).lastAngle >= TWO_PI) ((Coin) tile).lastAngle = 0;
                    }
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE) {
                        game.applet.rotate(PI,0,1,0);
                    }
                    if (tile instanceof Key) {
                        game.applet.rotate(((Key) tile).lastAngle+=0.1f);
                        if (((Key) tile).lastAngle >= TWO_PI) ((Key) tile).lastAngle = 0;
                    }
                    if (tile instanceof Breakable) {
                        Breakable breakTile = (Breakable) tile;
                        if (!breakTile.broken()) {
                            if (breakTile.breaking) TileType.BREAKABLE.model.drawModel();
                            else TileType.BLOCK.model.drawModel();
                        }
                    } else {
                        if (tile.type == TileType.EXIT) {
                            game.applet.rotate(lastFlagAngle+=0.1);
                            if (lastFlagAngle >= TWO_PI) lastFlagAngle = 0;
                        }
                        tile.type.model.drawModel();
                    }
                }
                game.applet.popMatrix();
            }
        }
    }
}
