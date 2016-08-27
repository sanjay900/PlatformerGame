package menu;

/**
 * Created by sanjay on 27/08/2016.
 */

import processing.core.PApplet;

/**
 * Created by surface on 26/08/2016.
 */
public class Slider {
    int swidth, sheight;    // width and height of bar
    float xpos, ypos;       // x and y position of bar
    float spos, newspos;    // x position of slider
    float sposMin, sposMax; // max and min values of slider
    int loose;              // how loose/heavy
    boolean over;           // is the mouse over the slider?
    boolean locked;
    float ratio;
    String name;;
    PApplet applet;
    public Slider(float xp, float yp, int sw, int sh, int l, String name, PApplet applet) {
        this.applet = applet;
        swidth = sw;
        sheight = sh;
        int widthtoheight = sw - sh;
        ratio = (float)sw / (float)widthtoheight;
        xpos = xp;
        ypos = yp-sheight/2;
        spos = xpos + swidth/2 - sheight/2;
        newspos = spos;
        sposMin = xpos;
        sposMax = xpos + swidth - sheight;
        loose = l;
        this.name = name;
    }

    public void update() {
        over = overEvent();
        if (applet.mousePressed && over) {
            locked = true;
        }
        if (!applet.mousePressed) {
            locked = false;
        }
        if (locked) {
            newspos = constrain(applet.mouseX-sheight/2, sposMin, sposMax);
        }
        if (PApplet.abs(newspos - spos) > 1) {
            spos = spos + (newspos-spos)/loose;
        }
    }

    private float constrain(float val, float minv, float maxv) {
        return PApplet.min(PApplet.max(val, minv), maxv);
    }

    boolean overEvent() {
        if (applet.mouseX > xpos && applet.mouseX < xpos+swidth &&
                applet.mouseY > ypos && applet.mouseY < ypos+sheight) {
            return true;
        } else {
            return false;
        }
    }

    public void display() {
        applet.fill(255);
        applet.text(name,xpos-100,ypos+50);
        applet.noStroke();
        applet.fill(204);
        applet.rect(xpos, ypos, swidth, sheight);
        if (over || locked) {
            applet.fill(0, 0, 0);
        } else {
            applet.fill(102, 102, 102);
        }
        applet.rect(spos, ypos, sheight, sheight);
    }

    public float getPos() {
        // Convert spos to be values between
        // 0 and the total width of the scrollbar
        return spos-xpos;
    }
}