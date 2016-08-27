package game;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import menu.Slider;
import levels.LevelParser;
import levels.Map;
import menu.Button;
import tiles.TileType;
import com.sanjay900.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
    int deaths = 0;
    int coins = 0;
    Mode mode = Mode.MENU;
    Map current;
    public Player player;
    List<Button> buttons = new ArrayList<>();
    PImage background;
    PImage backgroundIngame;
    PImage header;
    MD2Model model;
    Slider slider;
    Importer importer = new Importer();
    public static void main(String[] args) {
        ProcessingRunner.run(new Game());
    }
    public void settings() {
        size(800,600,P3D);
    }
    public void keyPressed() {
        if (key == ESC) {
            Map.levelNum = 6;
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
    public void setup() {
        player = new Player(0,0,this);
        backgroundIngame = loadImage("assets/BACK.png");
        background = loadImage("assets/menuwood.png");
        header = loadImage("assets/temp_banner_480.png");
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
        buttons.add(temp=new Button(this,250,340,300,100,"Play"));
        temp.setOnMouseClicked(()->mode=Mode.GAME);
        buttons.add(temp=new Button(this,250,450,300,100,"Quit"));
        temp.setOnMouseClicked(this::exit);
        slider = new Slider(250,240,400,100,1,this);
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
        slider.update();
        slider.display();
    }
    private void drawGame() {
        hint(PConstants.ENABLE_DEPTH_TEST);
        background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
        textSize(40);
        hint(PConstants.DISABLE_DEPTH_TEST);
        text("Deaths: "+deaths+"    Coins: "+coins,250,40);
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
