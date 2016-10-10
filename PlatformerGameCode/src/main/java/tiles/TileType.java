package tiles;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import game.Game;

import java.awt.*;
import java.io.IOException;


public enum TileType {
 //TODO: these should all be unique colors
    BLOCK(Color.BLACK),SPIKE(Color.RED), EXIT(Color.YELLOW,new Animation(1, 0, 2f, 0.1f)),
    LEFT_SPIKE(new Color(200,0,0)), RIGHT_SPIKE(new Color(225,0,0)),
    UPSIDE_DOWN_SPIKE(new Color(127, 0, 0)), BREAKABLE(Color.GRAY),
    KEY(new Color(255,0,220)),
    COIN(Color.BLUE ),KEY_SLOT(Color.CYAN),
    BREAKFAST(new Color(128,128,0)),
    BOX(new Color(127,51,0)),
    TELEPORTER_BLACK(new Color(128,51,0)),
    TELEPORTER_BLUE(new Color(128,52,0)),
    TELEPORTER_CYAN(new Color(128,53,0)),
    TELEPORTER_GRAY(new Color(128,54,0)),
    TELEPORTER_GREEN(new Color(128,55,0)),
    TELEPORTER_PURPLE(new Color(128,56,0)),
    TELEPORTER_RED(new Color(128,57,0)),
    TELEPORTER_YELLOW(new Color(128,58,0)),

    BUTTON_ON_CYAN_CIRC(new Color(0,201,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_CYAN(new Color(0,202,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_MAGENTA_CIRC(new Color(0,203,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_MAGENTA(new Color(0,204,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_RED_CIRC(new Color(0,205,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_RED(new Color(0,206,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_WHITE_CIRC(new Color(0,207,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_WHITE(new Color(0,208,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_YELLOW_CIRC(new Color(0,209,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_YELLOW(new Color(0,210,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_LIME_CIRC(new Color(0,211,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_LIME(new Color(0,212,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_BROWN_CIRC(new Color(0,213,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_BROWN(new Color(0,214,255),new Animation(79, 0, 1f, 1)),

    BUTTON_ON_BLUE_CIRC(new Color(0,215,255),new Animation(1, 0, 0.5f, 0.1f)),
    GATE_BLUE(new Color(0,216,255),new Animation(79, 0, 1f, 1)),

    //These models exist as states for other models, and as a result never actually appear in map images. They can be
    //black and the loop will find BLOCK first, never drawing these directly.
    BREAK2(new Color(0,0,0)),
    KEY_SLOT_FILLED(new Color(0,0,0)),
    BUTTON_OFF_LIME_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_MAGENTA_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_RED_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_WHITE_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_YELLOW_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_BROWN_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_BLUE_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f)),
    BUTTON_OFF_CYAN_CIRC(new Color(0,0,0),new Animation(1, 1, 0.5f, 0.1f));
    public final Animation animation;
    public MD2Model model;
    public int color;
    TileType(Color color) {
        this(color,new Animation(1,0,1,0.1f));
    }
    TileType(Color color, Animation animation) {
        this.color = color.getRGB();
        this.animation = animation;
    }
    public void loadModel(Game game) {
        String name = name();
        if (this.name().contains("SPIKE")) name = "spike";
        if (this == BREAKFAST) name = "breakable";
        try {
            if (this.name().contains("TELEPORTER")) {
                name = name.replace("TELEPORTER_","");
                model = game.getImporter().importModel(game.resolve("assets/models/block.md2"), game.applet.loadImage("assets/textures/teleporter/" + name.toLowerCase() + ".jpg"), game.applet);
            } else if (this.name().contains("BUTTON")) {
                name = name.replace("BUTTON_","");
                if (name.contains("CIRC"))
                    model = game.getImporter().importModel(game.resolve("assets/models/circ.md2"), game.applet.loadImage("assets/textures/buttons/" + name.toLowerCase() + ".jpeg"), game.applet);
                else
                    model = game.getImporter().importModel(game.resolve("assets/models/button.md2"), game.applet.loadImage("assets/textures/buttons/" + name.toLowerCase() + ".jpeg"), game.applet);
            } else if (this.name().contains("GATE")){
                name = name.replace("GATE_","");
                model = game.getImporter().importModel(game.resolve("assets/models/gate.md2"), game.applet.loadImage("assets/textures/gates/" + name.toLowerCase() + ".png"), game.applet);
            } else {
                model = game.getImporter().importModel(game.resolve("assets/models/" + name.toLowerCase() + ".md2"), game.applet.loadImage("assets/textures/models/" + name.toLowerCase() + ".png"), game.applet);
            }
            model.setAnimation(animation, 2f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
