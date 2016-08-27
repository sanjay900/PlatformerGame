package game;

import MD2.MD2Model;
import processing.core.PVector;
import tiles.*;

import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.SHIFT;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Player {
    static final float BOUNDING_BOX_MODIFIER = 1.15f;
    MD2Model model;
    Game game;
    boolean dontMove = false;
    public PVector position;
    float playerWidth;
    float playerHeight;
    float drag = 0.75f;
    float acceleration = 2f;
    float accelerationFrozen = 0.5f;
    float jump = 12f;
    PVector gravity = new PVector(0,20/30f);
    PVector velocity = new PVector(0,0);
    public Player(float x, float y, Game game) {
        playerHeight = BOUNDING_BOX_MODIFIER *(game.height/24);
        playerWidth = game.width/32;
        position = new PVector(x,y);
        this.game = game;
    }

    public void readImages(Game applet) {
        try {
            model = applet.importer.importModel(new File("assets/models/bob.md2"),applet.loadImage("assets/models/bob_3d2.png"),applet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updatePosition() {
        boolean ground = false;
        if (!dontMove)
        velocity.add(gravity);

        if (!dontMove)
            position.add(velocity.x,0);
        if (!dontMove)
        velocity = new PVector(velocity.x*drag,velocity.y);

        ArrayList<Tile> collided = collides();
        if (!collided.isEmpty()) {
            Optional<Tile> test = collided.stream().filter(c -> c.bounds.intersects(getBounds())).findAny();
            if (test.isPresent()) {
                if (velocity.x > 0f) {
                    position = new PVector(test.get().bounds.x - (playerWidth), position.y);
                } else if (velocity.x < 0f) {
                    position = new PVector(test.get().bounds.x + (playerWidth), position.y);
                }
                velocity = new PVector(0, velocity.y);
            }
        }
        if (!dontMove)
        position.add(0,velocity.y);
        collided = collides();
        if (!collided.isEmpty()) {
            Optional<Tile> test = collided.stream().filter(c -> c.bounds.intersects(getBounds())).findAny();
            if (test.isPresent()) {
                ground = true;
                if (velocity.y > 0) {
                    position = new PVector(position.x, test.get().bounds.y - playerHeight);
                } else if (velocity.y < 0) {
                    position = new PVector(position.x, test.get().bounds.y + playerHeight);
                }
                velocity = new PVector(velocity.x, 0);
            }
        }
        if (left) {
            velocity.add(-acceleration,0);
        }
        if (right) {
            velocity.add(acceleration,0);
        }
        if ((down && dontMove)) {
            velocity.add(0, acceleration);
        }
        if ((up && (ground || dontMove))) {
            velocity.add(0, !dontMove?-jump:-accelerationFrozen);
            ground = false;
        }
        if (ground) {
            if (Math.abs(velocity.x) < 0.2f) {
                model.stopAnimation();
            } else {
                model.startAnimation();
            }
            if (last != AnimationCycles.WALKING) {
                model.setAnimation((last= AnimationCycles.WALKING).getAnimation(),2f);
            }
        } else if (last != AnimationCycles.JUMP) {
            model.setAnimation((last= AnimationCycles.JUMP).getAnimation(),2f);
        }
        int lineFront = 20;
        game.pushMatrix();
        game.stroke(0);
        game.strokeWeight(1.25f);
        game.line(position.x + playerWidth/2, position.y, position.z + lineFront, (velocity.x + position.x) + playerWidth/2, (velocity.y + position.y), (velocity.z + position.z) + lineFront);
        game.scale(4);
        game.noStroke();
        game.popMatrix();
    }
    private AnimationCycles last = AnimationCycles.WALKING;

    void die() {
        position = new PVector(game.current.playerStart.bounds.x, game.current.playerStart.bounds.y);
        velocity = new PVector();
        game.current.breakables.forEach(Breakable::reset);
        game.current.keys.forEach(Key::reset);
        for (Tile[] platform : game.current.platforms) {
            for (Tile tile : platform) {
                if (tile != null && tile.type == TileType.KEY_SLOT_FILLED) tile.type = TileType.KEY_SLOT;
                if (tile instanceof Coin) ((Coin) tile).reset();
            }
        }
        game.deaths++;
        game.coins = 0;
    }
    private ArrayList<Tile> collides() {
        ArrayList<Tile> collide = new ArrayList<>();
        for (int y = 0; y < game.current.platforms.length; y++) {
            for (Tile tile : game.current.platforms[y]) {
                if (tile == null) continue;
                if (tile.getBounds().intersects(getBounds())) {
                    if (tile instanceof Key) {
                        ((Key) tile).gotten = true;
                        continue;
                    }
                    if (tile instanceof Coin) {
                        if (!((Coin) tile).gotten) game.coins++;
                        ((Coin) tile).gotten = true;
                        continue;
                    }
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE || tile.type == TileType.SPIKE)  {
                        die();
                        collide.clear();
                        return collide;
                    }
                    if (tile.type == TileType.EXIT) {
                        if (game.current.keys.stream().anyMatch(key -> !key.gotten)) continue;
                        game.nextLevel();
                        collide.clear();
                        velocity = new PVector();
                        return collide;
                    }
                    if (tile instanceof Breakable) {
                        Breakable breakTile = (Breakable) tile;
                        if (breakTile.broken()) continue;
                        else if (!breakTile.breaking) breakTile.startBreak();
                    }
                    collide.add(tile);
                }

            }
        }
        return collide;
    }
    public Rectangle2D.Float getBounds() {
        return new Rectangle2D.Float(position.x, position.y,playerWidth,playerHeight);
    }
    public void draw() {
        game.pushMatrix();
        game.translate(position.x+playerWidth/2,position.y+playerHeight);
        game.rotateX(HALF_PI);
        if(velocity.x > 0) game.rotateZ(PI);
        model.drawModel();
        game.popMatrix();
    }
    boolean up = false;
    boolean left = false;
    boolean right = false;
    boolean down = false;
    public void keyPressed() {
        dontMove = dontMove || game.keyCode == SHIFT;
        right = game.key == 'd' || game.keyCode == KeyEvent.VK_RIGHT || right;
        left = game.key == 'a' || game.keyCode == KeyEvent.VK_LEFT ||left;
        down = game.key == 's' || game.keyCode == KeyEvent.VK_DOWN ||down;
        up = game.key == 'w' || game.keyCode == KeyEvent.VK_UP || game.keyCode == KeyEvent.VK_SPACE || up;
    }
    public void keyReleased() {
        if (game.keyCode == SHIFT) dontMove = false;
        if (game.key == 'D' || game.key == 'd' || game.keyCode == KeyEvent.VK_RIGHT ) right = false;
        if (game.key == 'A' || game.key == 'a' || game.keyCode == KeyEvent.VK_LEFT) left = false;
        if (game.key == 'S' || game.key == 's' || game.keyCode == KeyEvent.VK_DOWN) down = false;
        if (game.key == 'W' || game.key == 'w' || game.keyCode == KeyEvent.VK_UP || game.keyCode == KeyEvent.VK_SPACE ) up = false;
        if (game.key == 'P' || game.key == 'R' || game.key == 'p' || game.key == 'r') die();
    }
}
