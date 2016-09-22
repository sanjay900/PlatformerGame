package game;

import MD2.MD2Model;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PVector;
import tiles.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static processing.core.PConstants.*;

/**
 * Created by sanjay on 26/08/2016.
 */
@Getter
public class Player {
    static final float BOUNDING_BOX_MODIFIER = 1.15f;
    MD2Model model;
    Game game;
    boolean dontMove = false;
    public PVector position;
    Consumer<String> playSound;
    float playerWidth;
    float playerHeight;
    float drag = 0.75f;
    float acceleration = 2f;
    float accelerationFrozen = 0.5f;
    float jump = 12f;
    PVector gravity = new PVector(0,20/30f);
    PVector velocity = new PVector(0,0);
    public Player(float x, float y, Game game) {
        playSound = game.playSoundStr;
        playerHeight = BOUNDING_BOX_MODIFIER *(game.applet.height/24);
        playerWidth = game.applet.width/32;
        position = new PVector(x,y);
        this.game = game;
    }

    public void readImages(Game applet) {
        try {
            model = applet.importer.importModel(applet.resolve("assets/models/bob.md2"),applet.applet.loadImage("assets/models/bob_3d2.png"),game.applet);
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
                    position = new PVector(test.get().bounds.getX() - (playerWidth), position.y);
                } else if (velocity.x < 0f) {
                    position = new PVector(test.get().bounds.getX() + (playerWidth), position.y);
                }
                velocity = new PVector(0, velocity.y);
            }
        }
        if (!dontMove)
            position.add(0,velocity.y);
        collided = collides();
        boolean top = false;
        if (!collided.isEmpty()) {
            Optional<Tile> test = collided.stream().filter(c -> c.bounds.intersects(getBounds())).findAny();
            if (test.isPresent()) {
                ground = true;
                if (test.get().bounds.getY()-getBounds().getY()<0) {
                    top = up = up2;
                }
                if (velocity.y > 0) {
                    position = new PVector(position.x, test.get().bounds.getY() - playerHeight);
                } else if (velocity.y < 0) {
                    position = new PVector(position.x, test.get().bounds.getY() + playerHeight);
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

        if ((up && ground) || (up2 && dontMove)) {
            if (!top) {
                up = false;
                if (!dontMove)
                    playSound.accept("JUMP.wav");
            }
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
        game.applet.pushMatrix();
        game.applet.stroke(0);
        game.applet.strokeWeight(1.25f);
        game.applet.line(position.x + playerWidth/2, position.y, position.z + lineFront, (velocity.x + position.x) + playerWidth/2, (velocity.y + position.y), (velocity.z + position.z) + lineFront);
        game.applet.scale(4);
        game.applet.noStroke();
        game.applet.popMatrix();
        if (getBounds().getX()<=-10) die();
        if (getBounds().getX()+10+getBounds().getWidth()>=game.current.platforms.length*getBounds().getWidth()) die();
        if (getBounds().getY()<=-10) die();
        if (getBounds().getY()+10+getBounds().getHeight()>=game.current.platforms.length*getBounds().getHeight()) die();
    }
    private AnimationCycles last = AnimationCycles.WALKING;
    public void start(){
        up = down = left = right = false;
        position = new PVector(game.current.playerStart.bounds.getX(), game.current.playerStart.bounds.getY());
        velocity = new PVector();
        game.current.breakables.forEach(Breakable::reset);
        game.current.keys.forEach(Key::reset);
        for (Tile[] platform : game.current.platforms) {
            for (Tile tile : platform) {
                if (tile != null && tile.type == TileType.KEY_SLOT_FILLED) tile.type = TileType.KEY_SLOT;
                if (tile instanceof Coin) ((Coin) tile).reset();
            }
        }
    }
    void die() {
        playSound.accept("DIE.wav");
        start();
        game.deaths++;
        game.currentPack.failLevel();
        game.coins = 0;
    }
    void restart() {
        start();
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
                        if (!((Key) tile).gotten)
                            playSound.accept("KEY.wav");
                        ((Key) tile).gotten = true;
                        continue;
                    }
                    if (tile instanceof Coin) {
                        if (!((Coin) tile).gotten) {
                            playSound.accept("COIN.wav");
                            game.coins++;
                        }
                        ((Coin) tile).gotten = true;
                        continue;
                    }
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE || tile.type == TileType.SPIKE || tile.type == TileType.LEFT_SPIKE || tile.type == TileType.RIGHT_SPIKE)  {
                        die();
                        collide.clear();
                        return collide;
                    }
                    if (tile.type == TileType.EXIT) {
                        if (game.current.keys.stream().anyMatch(key -> !key.gotten)) continue;
                        playSound.accept("FLAG.wav");
                        game.currentPack.completeLevel();
                        game.nextLevel();
                        collide.clear();
                        velocity = new PVector();
                        return collide;
                    }
                    if (tile instanceof Breakable) {
                        Breakable breakTile = (Breakable) tile;
                        if (breakTile.broken()) continue;
                        else if (!breakTile.breaking) {
                            breakTile.startBreak();
                            playSound.accept("bREAK.WAV");
                        }
                    }
                    collide.add(tile);
                }

            }
        }
        return collide;
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D(position.x, position.y,playerWidth,playerHeight);
    }
    public void draw() {
        game.applet.pushMatrix();
        game.applet.translate(position.x+playerWidth/2,position.y+playerHeight);
        game.applet.rotateX(HALF_PI);
        if(velocity.x > 0) game.applet.rotateZ(PI);
        model.drawModel();
        game.applet.popMatrix();
    }
    boolean up = false;
    boolean up2 = false;
    boolean left = false;
    boolean right = false;
    boolean down = false;
    public void keyPressed() {
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        dontMove = dontMove || game.applet.keyCode == SHIFT;
        right = game.applet.key == 'd' || game.applet.keyCode == RIGHT || right;
        left = game.applet.key == 'a' || game.applet.keyCode == LEFT ||left;
        down = game.applet.key == 's' || game.applet.keyCode == DOWN ||down;
        up = game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' '||up;
        up2 = game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' '||up;
    }
    public void keyReleased() {
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        if (game.applet.keyCode == SHIFT) dontMove = false;
        if (game.applet.key == 'd' || game.applet.keyCode == RIGHT ) right = false;
        if (game.applet.key == 'a' || game.applet.keyCode == LEFT) left = false;
        if (game.applet.key == 's' || game.applet.keyCode == DOWN) down = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' ') up = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' ') up2 = false;
        if (game.applet.key == 'p' || game.applet.key == 'r') restart();
    }
}
