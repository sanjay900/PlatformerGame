package net.tangentmc.processing;

import processing.core.PApplet;

/**
 * Created by sanjay on 21/08/16.
 */
public class ProcessingRunner {
    public static PApplet instance;
    public static void run(PApplet applet) {
        PApplet.runSketch(new String[]{applet.getClass().getName()},instance=applet);
    }
}
