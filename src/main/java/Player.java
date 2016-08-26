import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Player {
    Game game;
    PVector position;
    int playerWidth;
    int playerHeight;
    PVector gravity = new PVector(0,20/30f);
    PVector velocity = new PVector(0,0);
    public Player(float x, float y, Game game) {
        playerHeight = 2*(game.height/24);
        playerWidth = game.width/32;
        position = new PVector(x,y);
        this.game = game;
    }
   public void updatePosition() {
        boolean ground = false;
        velocity.add(gravity);
        position.add(velocity.x,0);
        velocity = new PVector(velocity.x*0.6f,velocity.y);
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
                    position = new PVector(position.x, test.get().bounds.y + (playerHeight / 2));
                }
                velocity = new PVector(velocity.x, 0);
            }
        }
        if (left) {
            velocity.add(-4f,0);
        }
        if (right) {
            velocity.add(4f,0);
        }

        if (up && ground) {
            velocity.add(0, -13f);
        }
    }
    private ArrayList<Tile> collides() {
        ArrayList<Tile> collide = new ArrayList<>();
        for (int y = 0; y < game.current.platforms.length; y++) {
            for (Tile tile : game.current.platforms[y]) {
                if (tile == null) continue;
                if (tile.getBounds().intersects(getBounds())) collide.add(tile);
            }
        }
        return collide;
    }
    public Rectangle2D.Float getBounds() {
        return new Rectangle2D.Float(position.x, position.y,playerWidth,playerHeight);
    }
    public void draw() {
        game.fill(255,0,0);
        game.rect(position.x,position.y,playerWidth,playerHeight);
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
