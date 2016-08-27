import MD2.MD2Model;
import processing.core.PVector;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.PI;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Player {
    static final float BOUNDING_BOX_MODIFIER = 1.15f;
    MD2Model model;
    Game game;
    PVector position;
    float playerWidth;
    float playerHeight;
    float drag = 0.75f;
    float momentum = 2f;
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
        velocity.add(gravity);
        position.add(velocity.x,0);
        velocity = new PVector(velocity.x*drag,velocity.y);
        ArrayList<Tile> collided = collides();
        if (!collided.isEmpty()) {
            Optional<Tile> test = collided.stream().filter(c -> c.bounds.intersects(getBounds())).findAny();
            if (test.isPresent()) {
                if (velocity.x > 0) {
                    position = new PVector(test.get().bounds.x - (playerWidth), position.y);
                } else if (velocity.x < 0) {
                    position = new PVector(test.get().bounds.x + (playerWidth), position.y);
                }
                velocity = new PVector(0, velocity.y);
            }
        }
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
            velocity.add(-momentum,0);
        }
        if (right) {
            velocity.add(momentum,0);
        }

        if (up && ground) {
            velocity.add(0, -jump);
            ground = false;
        }
        if (ground) {
            if (Math.abs(velocity.x) < 0.2f) {
                model.stopAnimation();
            } else {
                model.startAnimation();
            }
            if (last != AnimationCycles.WALKING) {
                model.setAnimation((last=AnimationCycles.WALKING).getAnimation(),2f);
            }
        } else if (last != AnimationCycles.JUMP) {
            model.setAnimation((last=AnimationCycles.JUMP).getAnimation(),2f);
        }
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
            }
        }
        game.deaths++;
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
    public void keyPressed() {
        right = game.key == 'd' || right;
        left = game.key == 'a' || left;
        up = game.key == 'w' || up;
    }
    public void keyReleased() {
        if (game.key == 'd') right = false;
        if (game.key == 'a') left = false;
        if (game.key == 'w') up = false;
        if (game.key == 'p') die();
    }
}
