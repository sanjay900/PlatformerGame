import com.sanjay900.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
    Mode mode = Mode.MENU;
    List<Map> maps = new ArrayList<>();
    Map current;
    Player player;
    List<Button> buttons = new ArrayList<>();
    PImage background;
    PImage header;
    public static void main(String[] args) {
        ProcessingRunner.run(new Game());
    }
    public void settings() {
        size(800,600);
    }
    public void keyPressed() {
        player.keyPressed();
    }
    public void keyReleased() {
        player.keyReleased();
    }
    public void setup() {
        background = loadImage("menuwood.png");
        header = loadImage("temp_banner_480.png");
        noStroke();
        for (TileType tileType : TileType.values()) {
            tileType.loadImage(this);
        }
        maps.add(LevelParser.parseLevel(this,Map.levelNum++));
        player.readImages(this);
        current = maps.get(0);
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
    void drawMenu() {
        background(background);
        image(header,100,100,width-200,200);
        buttons.forEach(Button::draw);
    }
    void drawGame() {
        background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
    }
    public enum Mode {
        MENU,GAME
    }
}
