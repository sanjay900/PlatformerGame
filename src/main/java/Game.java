import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
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
    Mode mode = Mode.MENU;
    Map current;
    Player player;
    List<Button> buttons = new ArrayList<>();
    PImage background;
    PImage header;
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
            Map.levelNum = 1;
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
        background = loadImage("assets/menuwood.png");
        header = loadImage("assets/temp_banner_480.png");
        try {
            model = importer.importModel(new File("assets/models/block.md2"),loadImage("assets/models/block.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.BLOCK.loadModel(model);
            model = importer.importModel(new File("assets/models/block.md2"),loadImage("assets/models/break.png"),this);

            model.setAnimation(new Animation(1,0,1,0.1f),2f);
            TileType.BREAKABLE.loadModel(model);
            model = importer.importModel(new File("assets/models/sticky.md2"),loadImage("assets/models/sticky.png"),this);

            TileType.UPSIDE_DOWN_SPIKE.loadModel(model);
            TileType.SPIKE.loadModel(model);
            model.setAnimation(new Animation(1,0,1,0.1f),2f);

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
        hint(PConstants.ENABLE_DEPTH_TEST);
        background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
        textSize(40);
        hint(PConstants.DISABLE_DEPTH_TEST);
        text("Death Counter: "+deaths,50,40);
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
