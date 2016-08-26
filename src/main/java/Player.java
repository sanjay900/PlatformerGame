import processing.core.PVector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Optional;
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
        velocity.add(gravity);
        position.add(velocity);
        Tile collided = collides();
        if (collided != null){
            if (collided.bounds.y-getBounds().y > 0)
                position = new PVector(position.x,collided.bounds.y-playerHeight);
            else
                position = new PVector(position.x,collided.bounds.y+(playerHeight/2));
            velocity = new PVector(0,0);
        }
        if (game.keyPressed) {
            if (game.key == 'w') {
                if (collided != null){
                    velocity.add(0,-20f);
                }
            }
            if (game.key == 'a') {
                position = new PVector(position.x-6,position.y);
                collided = collides();
                if (collided != null){
                    position = new PVector(collided.bounds.x+playerWidth,position.y);
                }
            }
            if (game.key  == 'd') {
                position = new PVector(position.x+6,position.y);
                collided = collides();
                if (collided != null){
                    position = new PVector(collided.bounds.x-playerWidth,position.y);
                }
            }
        }
    }
    Tile collides() {
        for (int y = 0; y < game.current.platforms.length; y++) {
            for (Tile tile : game.current.platforms[y]) {
                if (tile == null) continue;
                if (tile.getBounds().intersects(getBounds())) return tile;
            }
        }
        return null;
    }
    public Rectangle2D.Float getBounds() {
        return new Rectangle2D.Float(position.x, position.y,playerWidth,playerHeight);
    }
    public void draw() {
        game.fill(255,0,0);
        game.rect(position.x,position.y,playerWidth,playerHeight);
    }
}
