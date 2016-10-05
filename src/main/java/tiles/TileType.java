package tiles;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import game.Game;

import java.awt.*;
import java.io.IOException;


public enum TileType {
    BLOCK(Color.BLACK),SPIKE(Color.RED), EXIT(Color.YELLOW,new Animation(1, 0, 2f, 0.1f)),
    LEFT_SPIKE(new Color(200,0,0)), RIGHT_SPIKE(new Color(225,0,0)),
    UPSIDE_DOWN_SPIKE(new Color(127, 0, 0)), BREAKABLE(Color.GRAY),
    KEY(new Color(255,0,220),new Animation(1, 0, 0.2f, 0.1f)),
    COIN(Color.BLUE,new Animation(1, 0, 0.2f, 0.1f)),KEY_SLOT(Color.CYAN),
    KEY_SLOT_FILLED(new Color(0,1,2)),
    BREAKFAST(new Color(128,128,0));
    private final Animation animation;
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
            model = game.getImporter().importModel(game.resolve("assets/models/"+name.toLowerCase()+".md2"),game.applet.loadImage("assets/textures/models/"+name.toLowerCase()+".png"), game.applet);
            model.setAnimation(animation, 2f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
