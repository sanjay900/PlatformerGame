import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import processing.awt.PShapeJava2D;
import processing.core.PApplet;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Created by surface on 26/08/2016.
 */
public class TheButton {
    private Runnable onClicked;
    private PApplet game;
    private Rectangle wildRBeast;
    private TheButton that;

    public TheButton(PApplet app, double x, double y, double width, double height) {
        wildRBeast = new Rectangle((int)x, (int)y, (int)width, (int)height);
        game = app;
        that=this;
    }

    public TheButton(PApplet app, float x, float y, float width, float height) {
        wildRBeast = new Rectangle((int)x, (int)y, (int)width, (int)height);
        game = app;
        that=this;
    }

    public TheButton(PApplet app, int x, int y, int width, int height) {
        wildRBeast = new Rectangle(x, y, width, height);
        game = app;
        that=this;
    }

    public  TheButton(PApplet app, Rectangle bounds) {
        game = app;
        wildRBeast = bounds;
        that = this;
    }


    private class Checker implements Runnable {
        @Override
        public void run() {
            while(true) {
                if(game.mousePressed&&wildRBeast.contains(game.mouseX,game.mouseY)) {
                    if (onClicked != null)
                    onClicked.run();
                }
            }
        }
    }


    public final void setOnMouseClicked(Runnable value) {
        this.onClicked = value;
    }
}
