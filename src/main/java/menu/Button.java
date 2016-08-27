package menu;

import processing.core.PApplet;
import processing.core.PConstants;

import java.awt.*;

/**
 * Created by surface on 26/08/2016.
 */
public class Button {
    private Runnable onClicked;
    private PApplet game;
    private Rectangle bounds;
    String text;

    public Button(PApplet app, double x, double y, double width, double height, String text) {
        bounds = new Rectangle((int)x, (int)y, (int)width, (int)height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, float x, float y, float width, float height, String text) {
        bounds = new Rectangle((int)x, (int)y, (int)width, (int)height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, int x, int y, int width, int height, String text) {
        bounds = new Rectangle(x, y, width, height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, Rectangle bounds, String text) {
        game = app;
        this.bounds = bounds;
        this.text = text;
    }
    Color c = new Color(100,100,100);
    public void setColor(int r, int g, int b) {
        c = new Color(r,g,b);
    }
    float textSize = 32;
    public void draw() {
        game.fill(c.getRGB());
        if(bounds.contains(game.mouseX,game.mouseY)) {
            if (onClicked != null && game.mousePressed)
                onClicked.run();
            else {
                game.fill(200,200,200);
            }
        }
        game.stroke(0,0,0);
        game.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        game.textAlign(PConstants.CENTER);
        game.fill(0);
        game.textSize(textSize);
        game.text(text, bounds.x+ bounds.width/2, bounds.y+textSize/4+ bounds.height/2);
        game.noStroke();
    }
    public void run() {
        onClicked.run();
    }

    public final void setOnMouseClicked(Runnable value) {
        this.onClicked = value;
    }
}
