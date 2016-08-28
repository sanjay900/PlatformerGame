package game;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import com.sanjay900.ProcessingRunner;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import levels.LevelParser;
import levels.Map;
import menu.Button;
import menu.SelectionButton;
import menu.Slider;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.opengl.PGraphics3D;
import tiles.TileType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
    public boolean pauseBetween = false;
    int deaths = 0;
    int coins = 0;
    float flagAngle = 0;
    public Mode mode = Mode.MENU;
    public Map current;
    public Player player;
    List<Button> buttons = new ArrayList<>();
    List<SelectionButton> packs = new ArrayList<>();
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
        if (key ==ENTER && mode == Mode.MENU) {
            nextLevel();
            key = 0;
            player.die();
            deaths = 0;
        }
        if (key == ESC) {
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
        textFont(createFont("assets/munro.ttf",32));
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
            model = importer.importModel(new File("assets/models/dapokiy.obj.md2"),loadImage("assets/models/dapokiy.png"),this);

            TileType.UPSIDE_DOWN_SPIKE.loadModel(model);
            TileType.SPIKE.loadModel(model);
            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            model = importer.importModel(new File("assets/models/flag.md2"),loadImage("assets/models/FLAG.png"),this);
            TileType.EXIT.loadModel(model);
            model.setAnimation(new Animation(1,0,1f,0.1f),2f);
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
        player.readImages(this);
        player.model.setAnimation(AnimationCycles.WALKING.getAnimation(),2f);
        Button temp;
        buttons.add(temp=new Button(this,250,340,300,75,"Play"));
        temp.setOnMouseClicked(()->mode=Mode.SELECTION);
        buttons.add(temp=new Button(this,250,340+170,300,75,"Quit"));
        temp.setOnMouseClicked(()->{
            if (!pauseBetween) {
                exit();
            }
        });
        File[] files = new File("levels").listFiles();
        for (File f: files) {
            if (f.isDirectory()) {
                packs.add(new SelectionButton(f,this));
            }
        }
        currentPack = packs.get(0);
    }
    public void nextPack() {
        if (pauseBetween) return;
        pauseBetween = true;
        currentPack = packs.get((1+packs.indexOf(currentPack))%packs.size());
    }
    public void prevPack() {
        if (pauseBetween) return;
        pauseBetween = true;
        int index = (packs.indexOf(currentPack)-1)%packs.size();
        if (index <0) index = packs.size()+index;
        currentPack = packs.get(index);
    }
    public void mouseReleased() {
        pauseBetween =false;
    }
    public void draw() {
        if (mode == Mode.MENU) {
            drawMenu();
        } else if (mode == Mode.GAME) {
            drawGame();
        } else if (mode == Mode.SELECTION) {
            drawSelection();
        }

    }
    SelectionButton currentPack;
    private void drawSelection() {
        background(background);
        currentPack.render();
    }
    private void drawMenu() {

        ((PGraphics3D)g).textureSampling(3);
        player.model.drawModel();
        background(background);
        image(header,100,100,width-200,200);
        buttons.forEach(Button::draw);
    }
    private void drawGame() {

        ((PGraphics3D)g).textureSampling(5);
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
        fill(255);
        text("DEATHS: "+deaths+"    COINS: "+coins,250,35);
        if(player.dontMove) image(pauseScreen, 0, 0, width, height);
    }
    public void nextLevel() {
        currentPack.nextLevel();
    }



    public enum Mode {
        MENU,GAME,SELECTION
    }
}
