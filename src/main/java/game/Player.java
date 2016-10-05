package game;

import MD2.Animation;
import MD2.MD2Model;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PVector;
import tiles.*;

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
    static final String[] BOB_TMEMES = {"bob_3d2","grayden","jacob","Jesse","sanjay"};
    MD2Model model;
    Game game;
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
    PVector startPos;
    public Player(float x, float y, Game game) {
        playSound = game.soundResolver;
        playerHeight = BOUNDING_BOX_MODIFIER *(game.applet.height/24);
        playerWidth = game.applet.width/32;
        position = new PVector(x,y);
        startPos = position.copy();
        this.game = game;
        initModel(game);
    }

    private void initModel(Game applet) {
        try {
            model = applet.importer.importModel(applet.resolve("assets/models/bob.md2"),applet.applet.loadImage("assets/textures/character/"+BOB_TMEMES[game.players.size()]+".png"),game.applet);
            model.setAnimation(WALKING, 2f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void updatePosition() {
        boolean ground = false;
        velocity.add(gravity);
        position.add(velocity.x, 0);
        velocity = new PVector(velocity.x * drag, velocity.y);
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
        } else {
            Optional<Player> collide = game.players.stream().filter(b -> b != this && b.getBounds().intersects(getBounds())).findAny();
            if (collide.isPresent()) {
                if (velocity.x > 0f) {
                    position = new PVector(collide.get().getBounds().getX() - (playerWidth), position.y);
                } else if (velocity.x < 0f) {
                    position = new PVector(collide.get().getBounds().getX() + (playerWidth), position.y);
                }
                velocity = new PVector(0, velocity.y);
            }
        }

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
        } else {
            Optional<Player> collide = game.players.stream().filter(b -> b != this && b.getBounds().intersects(getBounds())).findAny();
            if (collide.isPresent()) {
                ground = true;
                if (collide.get().getBounds().getY()-getBounds().getY()<0) {
                    top = up = up2;
                }
                if (velocity.y > 0) {
                    position = new PVector(position.x, collide.get().getBounds().getY() - playerHeight);
                } else if (velocity.y < 0) {
                    position = new PVector(position.x, collide.get().getBounds().getY() + playerHeight);
                }
                velocity = new PVector(velocity.x, 0);
            }
        }
        if (left && game.currentPlayer == this) {
            velocity.add(-acceleration,0);
        }
        if (right && game.currentPlayer == this) {
            velocity.add(acceleration,0);
        }
        if ((down && game.currentPlayer == this)) {
            velocity.add(0, acceleration);
        }

        if ((up && ground)&&game.currentPlayer == this) {
            if (!top) {
                up = false;
                playSound.accept("JUMP.wav");
            }
            velocity.add(0,-jump);
            ground = false;
        }
        if (ground) {
            if (Math.abs(velocity.x) < 0.2f) {
                model.stopAnimation();
            } else {
                model.startAnimation();
            }
            if (last != WALKING) {
                model.setAnimation((last= WALKING),2f);
            }
        } else if (last != JUMP) {
            model.setAnimation((last= JUMP),2f);
        }

       /* Draw the line of the players momentum
        int lineFront = 20;
        game.applet.pushMatrix();
        game.applet.stroke(0);
        game.applet.strokeWeight(1.25f);
        game.applet.line(position.x + playerWidth/2, position.y, position.z + lineFront, (velocity.x + position.x) + playerWidth/2, (velocity.y + position.y), (velocity.z + position.z) + lineFront);
        game.applet.scale(4);
        game.applet.noStroke();
        game.applet.popMatrix();
        */
        //Some levels have secrets hidden near the edges, so we cant kill them as soon as they hit the border.
        int padding = 10;
        if (getBounds().getX()<=-padding) die();
        if (getBounds().getX()+padding+getBounds().getWidth()>=game.current.platforms.length*getBounds().getWidth()) die();
        if (getBounds().getY()<=-padding) die();
        if (getBounds().getY()+padding+getBounds().getHeight()>=game.current.platforms.length*getBounds().getHeight()) die();
    }
    public void start(){
        //Reset control variables when you start a level.
        up = up2 = down = left = right = false;
        position = startPos.copy();
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
    private void die() {
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
                    if (!tile.collide(this)) {
                        continue;
                    }
                    if (tile.type == TileType.UPSIDE_DOWN_SPIKE || tile.type == TileType.SPIKE || tile.type == TileType.LEFT_SPIKE || tile.type == TileType.RIGHT_SPIKE)  {
                        die();
                        collide.clear();
                        return new ArrayList<>();
                    }
                    if (tile.type == TileType.EXIT) {
                        if (!game.current.keys.stream().allMatch(Key::isGotten)) continue;
                        playSound.accept("FLAG.wav");
                        game.currentPack.completeLevel();
                        game.nextLevel();
                        collide.clear();
                        velocity = new PVector();
                        return collide;
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
    void draw() {
        game.applet.pushMatrix();
        game.applet.translate(position.x+playerWidth/2,position.y+playerHeight);
        game.applet.rotateX(HALF_PI);
        if(velocity.x > 0) game.applet.rotateZ(PI);
        model.drawModel();
        game.applet.popMatrix();
    }
    static boolean up = false;
    static boolean up2 = false;
    static boolean left = false;
    static boolean right = false;
    static boolean down = false;
    void keyPressed() {
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        right = game.applet.key == 'd' || game.applet.keyCode == RIGHT || right;
        left = game.applet.key == 'a' || game.applet.keyCode == LEFT ||left;
        down = game.applet.key == 's' || game.applet.keyCode == DOWN ||down;
        up = game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' '||up;
        up2 = game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' '||up;
    }
    void keyReleased() {
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        if (game.applet.key == 'd' || game.applet.keyCode == RIGHT ) right = false;
        if (game.applet.key == 'a' || game.applet.keyCode == LEFT) left = false;
        if (game.applet.key == 's' || game.applet.keyCode == DOWN) down = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' ') up = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || game.applet.key == ' ') up2 = false;
        if (game.applet.key == 'p' || game.applet.key == 'r') restart();
    }

    public void render() {
        model.drawModel();
    }

    public void highlight() {
        game.applet.noFill();
        game.applet.stroke(255,0,0);
        game.applet.rect(position.x-10, position.y-10,playerWidth+20,playerHeight+10);
        game.applet.noStroke();
    }

    private final Animation WALKING = new Animation(0,8,0,1,1,0.5f);
    private final Animation JUMP = new Animation(11,12,0,1,1,0.3f);
    private Animation last = WALKING;

    public void stop() {
        velocity = new PVector(0,0);
    }
}
