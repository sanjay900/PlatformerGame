package tiles;

import MD2.MD2Model;

import java.awt.*;


public enum TileType {
    BLOCK(Color.BLACK),SPIKE(Color.RED), EXIT(Color.YELLOW), LAVA(Color.ORANGE),
    UPSIDE_DOWN_SPIKE(new Color(127, 0, 0)), BREAKABLE(Color.GRAY),
    KEY(new Color(255,0,220)),COIN(Color.BLUE),KEY_SLOT(Color.CYAN), KEY_SLOT_FILLED(new Color(0,1,2)),
    BREAKFAST(new Color(128,128,0));
    public MD2Model model;
    public int color;
    TileType(Color color) {
        this.color = color.getRGB();
    }
    public void loadModel(MD2Model model) {
        this.model = model;
    }
}
