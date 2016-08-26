import com.sanjay900.ProcessingRunner;
import processing.core.PApplet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
    List<Map> maps = new ArrayList<>();
    Map current;
    Player player;
    public static void main(String[] args) {
        ProcessingRunner.run(new Game());
    }
    public void settings() {
        size(800,600);
    }
    public void setup() {
        noStroke();
        maps.add(new Map(new File("test1.txt"),this));
        current = maps.get(0);
    }
    public void draw() {
         background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
    }
}
