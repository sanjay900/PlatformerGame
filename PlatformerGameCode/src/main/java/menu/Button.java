package menu;

import net.tangentmc.collisions.Rectangle2D;
import processing.core.PApplet;
import processing.core.PConstants;

import java.awt.*;


/**
 * Created by surface on 26/08/2016.
 */
public class Button {
    private Runnable onClicked;
    private PApplet game;
    private Rectangle2D bounds;
    String text;

    public Button(PApplet app, double x, double y, double width, double height, String text) {
        bounds = new Rectangle2D((int)x, (int)y, (int)width, (int)height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, float x, float y, float width, float height, String text) {
        bounds = new Rectangle2D((int)x, (int)y, (int)width, (int)height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, int x, int y, int width, int height, String text) {
        bounds = new Rectangle2D(x, y, width, height);
        game = app;
        this.text = text;
    }

    public Button(PApplet app, Rectangle2D bounds, String text) {
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
        game.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        game.textAlign(PConstants.CENTER);
        game.fill(0);
        game.textSize(textSize);
        game.text(text, bounds.getX() + bounds.getWidth() /2, bounds.getY() +textSize/4+ bounds.getHeight() /2);
        game.noStroke();
    }
    public void run() {
        onClicked.run();
    }

    public final void setOnMouseClicked(Runnable value) {
        this.onClicked = value;
    }
}