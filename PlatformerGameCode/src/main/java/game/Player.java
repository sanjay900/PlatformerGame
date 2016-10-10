package game;

import MD2.Animation;
import MD2.MD2Model;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;
import processing.core.PVector;
import tiles.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static processing.core.PConstants.*;

@Getter
public class Player extends Tile {
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
    float jump = 12f;
    PVector gravity = new PVector(0,20/30f);
    PVector velocity = new PVector(0,0);
    PVector startPos;
    public Player(float x, float y, Game game) {
        super(null,null);
        playSound = sound -> game.soundResolver.accept(false,sound);
        playerHeight = BOUNDING_BOX_MODIFIER *(game.applet.height/24);
        playerWidth = (game.applet.height/24)*0.9f;
        position = new PVector(x,y);
        startPos = position.copy();
        //Jump was coded for block heights based on a resolution of 800. we should fix that.
        jump *= game.applet.height/800f;
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
        ArrayList<Tile> collided = collides(false, null);
        Tile test;
        if (!collided.isEmpty()) {
            test = collided.get(0);
            if (velocity.x > 0f) {
                position = new PVector(test.getBounds().getX() - (playerWidth), position.y);
            } else if (velocity.x < 0f) {
                position = new PVector(test.getBounds().getX() + (playerWidth) + (test instanceof Player?0:5), position.y);
            }
            velocity = new PVector(0, velocity.y);
        }
        position.add(0,velocity.y);
        collided = collides(true, null);
        boolean top = false;
        if (!collided.isEmpty()) {
            test = collided.get(0);
            ground = true;
            if (test.getBounds().getY()-getBounds().getY()<0) {
                top = up = up2;
            }
            if (velocity.y > 0) {
                position = new PVector(position.x, test.getBounds().getY() - playerHeight);
            } else if (velocity.y < 0) {
                position = new PVector(position.x, test.getBounds().getY() + playerHeight);
            }
            velocity = new PVector(velocity.x, 0);
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
        //Some levels have secrets hidden near the edges, so we cant kill them as soon as they hit the border.
        int padding = 10;
        if (getBounds().getY()<=-padding) die();
        if (getBounds().getY()+padding+getBounds().getHeight()>=game.current.xLength*getBounds().getHeight()) die();
        if (lastTp != null) {
            Rectangle2D playerRect = new Rectangle2D(position.x,position.y-playerHeight,playerWidth,playerHeight*4);
            if (!lastTp.getBounds().intersects(playerRect) && !lastTp.getLink().getBounds().intersects(playerRect)) {
                lastTp = null;
            }
        }
    }
    void start(){
        //Reset control variables when you start a level.
        up = up2 = down = left = right = false;
        position = startPos.copy();
        velocity = new PVector();
        game.current.breakables.forEach(Breakable::reset);
        game.current.keys.forEach(Key::reset);
        for (Tile tile : game.current.platforms) {
            if (tile.type == TileType.KEY_SLOT_FILLED) tile.type = TileType.KEY_SLOT;
            if (tile instanceof Coin) ((Coin) tile).reset();
            if (tile instanceof Button) ((Button) tile).reset();
            if (tile instanceof Gate) ((Gate) tile).reset();

        }

        game.current.boxs.forEach(Box::reset);
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
    private ArrayList<Tile> collides(boolean vert, Teleporter dest) {
        ArrayList<Tile> collide = new ArrayList<>();
        collide.addAll(game.current.boxs.stream().filter(box -> box.getBounds().intersects(getBounds())).filter(box -> vert||box.collide(this)).collect(Collectors.toList()));
        boolean boxFound = game.current.boxs.stream().filter(box -> box.getBounds().intersects(getBounds())).findAny().isPresent();

        for (Tile tile : game.current.platforms) {
            if (tile.getBounds().intersects(getBounds())) {
                if (tile instanceof Teleporter && boxFound) continue;
                if (tile == dest || !tile.collide(this)) {
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
        collide.addAll(game.players.stream().filter(pl -> pl != this && pl.getBounds().intersects(getBounds())).collect(Collectors.toList()));
        return collide;
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D(position.x, position.y,playerWidth,playerHeight);
    }
    void draw() {
        game.applet.pushMatrix();
        game.applet.translate(position.x+playerWidth/2,position.y+playerHeight);
        game.applet.scale(getBounds().getWidth()*0.75f,getBounds().getHeight()*0.63f,getBounds().getWidth()*0.75f);
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
        boolean space = game.applet.key == ' ';
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        right = game.applet.key == 'd' || game.applet.keyCode == RIGHT || right;
        left = game.applet.key == 'a' || game.applet.keyCode == LEFT ||left;
        down = game.applet.key == 's' || game.applet.keyCode == DOWN ||down;
        up = game.applet.key == 'w' || game.applet.keyCode == UP || space||up;
        up2 = game.applet.key == 'w' || game.applet.keyCode == UP || space||up;
    }
    void keyReleased() {
        boolean space = game.applet.key == ' ';
        game.applet.key = (game.applet.key + "").toLowerCase().charAt(0);
        if (game.applet.key == 'd' || game.applet.keyCode == RIGHT ) right = false;
        if (game.applet.key == 'a' || game.applet.keyCode == LEFT) left = false;
        if (game.applet.key == 's' || game.applet.keyCode == DOWN) down = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || space) up = false;
        if (game.applet.key == 'w' || game.applet.keyCode == UP || space) up2 = false;
        if (game.applet.key == 'p' || game.applet.key == 'r') restart();
    }

    void render() {
        model.drawModel();
    }

    void highlight() {
        game.applet.noFill();
        game.applet.stroke(255,0,0);
        game.applet.rect(position.x-10, position.y-10,playerWidth+20,playerHeight+10);
        game.applet.noStroke();
    }

    private final Animation WALKING = new Animation(0,8,0,1,0.05f,0.5f);
    private final Animation JUMP = new Animation(11,12,0,1,0.05f,0.3f);
    private Animation last = WALKING;

    void stop() {
        velocity = new PVector(0,0);
    }
    Teleporter lastTp = null;
    public void teleport(Teleporter dest) {
        if (lastTp == dest || lastTp == dest.getLink()) return;
        PVector old = position;
        this.position = new PVector(dest.getBounds().getX(),dest.getBounds().getY()-80);
        if (!collides(false,dest).isEmpty() || game.current.boxs.stream().anyMatch(s -> s.getBounds().intersects(getBounds()))) {
            position = old;
        }
        lastTp=dest;
    }
}
