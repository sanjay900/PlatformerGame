package game;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import com.sanjay900.ProcessingRunner;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import levels.LevelParser;
import levels.Map;
import menu.Button;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import tiles.TileType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
    boolean pauseBetween = false;
    int deaths = 0;
    int coins = 0;
    Mode mode = Mode.MENU;
    Map current;
    public Player player;
    List<Button> buttons = new ArrayList<>();
    PImage background;
    PImage backgroundIngame;
    PImage header;
    PImage pauseScreen;
    MD2Model model;
    Importer importer = new Importer();
    public static void main(String[] args) {
        ProcessingRunner.run(new Game());
    }
    public void settings() {
        size(800,600,P3D);
    }
    public void keyPressed() {
        if (key == ESC) {
            Map.levelNum = 0;
            nextLevel();
            key = 0;
            player.die();
            deaths = 0;
            mode = Mode.MENU;
        }
        player.keyPressed();
    }
    public void keyReleased() {
        player.keyReleased();
    }
    public void playSound(File f, boolean infinite) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Media media = new Media(f.toURI().toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        if(infinite) mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
    public void setup() {
        new Thread(() -> {
            new JFXPanel();
            playSound(new File("assets/RUBBER.mp3"), true);
        }).start();
                player = new Player(0, 0, this);
        backgroundIngame = loadImage("assets/BACK.png");
        background = loadImage("assets/menuwood.png");
        header = loadImage("assets/temp_banner_480.png");
        pauseScreen = loadImage("assets/pause.png");
        try {
            model = importer.importModel(new File("assets/models/block.md2"),loadImage("assets/models/block.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.BLOCK.loadModel(model);
            model = importer.importModel(new File("assets/models/keyholefill.md2"),loadImage("assets/models/KEYholefill.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.KEY_SLOT_FILLED.loadModel(model);
            model = importer.importModel(new File("assets/models/keyhole.md2"),loadImage("assets/models/KEYhole.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.KEY_SLOT.loadModel(model);
            model = importer.importModel(new File("assets/models/brokern.md2"),loadImage("assets/models/brokern.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.BREAKABLE.loadModel(model);
            model = importer.importModel(new File("assets/models/spikes.md2"),loadImage("assets/models/spikes.png"),this);

            TileType.UPSIDE_DOWN_SPIKE.loadModel(model);
            TileType.SPIKE.loadModel(model);
            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            model = importer.importModel(new File("assets/models/WORLD.md2"),loadImage("assets/models/WORLD.png"),this);
            TileType.EXIT.loadModel(model);
            model.setAnimation(new Animation(1,0,0.2f,0.1f),2f);
            model = importer.importModel(new File("assets/models/key.md2"),loadImage("assets/models/KEY.png"),this);
            TileType.KEY.loadModel(model);
            model.setAnimation(new Animation(1,0,0.2f,0.1f),2f);
            model = importer.importModel(new File("assets/models/coin.md2"),loadImage("assets/models/COIN.png"),this);
            TileType.COIN.loadModel(model);
            model.setAnimation(new Animation(1,0,0.2f,0.1f),2f);

        } catch (IOException e) {
            e.printStackTrace();
        }
        noStroke();
        nextLevel();
        player.readImages(this);
        player.model.setAnimation(AnimationCycles.WALKING.getAnimation(),2f);
        Button temp;
        buttons.add(temp=new Button(this,250,340,300,75,"Play"));
        temp.setOnMouseClicked(()->mode=Mode.GAME);
        buttons.add(temp=new Button(this,250,340+80,300,75,"Quit"));
        temp.setOnMouseClicked(()->{
            if (!pauseBetween) {
                exit();
            }
        });
    }
    public void mouseReleased() {
        pauseBetween =false;
    }
    public void draw() {
        if (mode == Mode.MENU) {
            drawMenu();
        } else if (mode == Mode.GAME) {
            drawGame();
        }

    }
    private void drawMenu() {
        player.model.drawModel();
        background(background);
        image(header,100,100,width-200,200);
        buttons.forEach(Button::draw);
    }
    private void drawGame() {
        pushMatrix();
        float scrollAmt = (float) (400-(player.getBounds().getX()+player.getBounds().getWidth()));
        float totalScroll = (float) (800-current.platforms.length*player.getBounds().getWidth());

        if (scrollAmt < 0) {
            translate(constrain(totalScroll,scrollAmt,0),0);
        }
        hint(PConstants.ENABLE_DEPTH_TEST);
        background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
        textSize(40);
        popMatrix();
        current.drawKeys();
        hint(PConstants.DISABLE_DEPTH_TEST);
        text("Deaths: "+deaths+"    Coins: "+coins,250,40);
        if(player.dontMove) image(pauseScreen, 0, 0, width, height);
    }
    public void nextLevel() {
        Map map =  LevelParser.parseLevel(this, Map.levelNum++);
        if (map != null)
            current = map;
    }



    public enum Mode {
        MENU,GAME
    }
}
