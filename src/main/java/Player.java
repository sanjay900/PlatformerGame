import MD2.MD2Model;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static processing.core.PConstants.*;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Player {
    static final float BOUNDING_BOX_MODIFIER = 1.15f;
    MD2Model model;
    static final float BOUNDING_BOX_MODIFIER = 1.265f;
    static PImage leftImage;
    static PImage rightImage;
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
        if (collided.stream().anyMatch(tile -> tile.type == TileType.SPIKE||tile.type == TileType.UPSIDE_DOWN_SPIKE)) die();
        if (collided.stream().anyMatch(tile -> tile.type == TileType.EXIT)) game.nextLevel();
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
        if (collided.stream().anyMatch(tile -> tile.type == TileType.SPIKE||tile.type == TileType.UPSIDE_DOWN_SPIKE))die();
        if (collided.stream().anyMatch(tile -> tile.type == TileType.EXIT)) game.nextLevel();
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
        }
        if (ground) model.setAnimation(AnimationCycles.WALKING.getAnimation());
        else model.setAnimation(AnimationCycles.JUMP.getAnimation());
    }

    private void die() {
        position = new PVector(game.current.playerStart.bounds.x, game.current.playerStart.bounds.y);
        velocity = new PVector();
        Map.breakables.forEach(Breakable::reset);
    }
    private ArrayList<Tile> collides() {
        ArrayList<Tile> collide = new ArrayList<>();
        for (int y = 0; y < game.current.platforms.length; y++) {
            for (Tile tile : game.current.platforms[y]) {
                if (tile == null) continue;
                if (tile.getBounds().intersects(getBounds())) {
                    if (tile instanceof Breakable) {
                        Breakable breakTile = (Breakable) tile;
                        if (breakTile.breaking()) continue;
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
        if(velocity.x < 0) game.rotateZ(PI);
        model.drawModel();
        game.popMatrix();
        if (velocity.x < 0) game.image(leftImage, position.x, position.y, leftImage.width, leftImage.height);
        else game.image(rightImage, position.x, position.y, rightImage.width, rightImage.height);
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
    }
}
