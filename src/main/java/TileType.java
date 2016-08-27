import MD2.MD2Model;

import java.awt.*;


public enum TileType {
    BLOCK(Color.BLACK),SPIKE(Color.RED), EXIT(Color.YELLOW),
    UPSIDE_DOWN_SPIKE(new Color(127, 0, 0)), BREAKABLE(Color.GRAY),
    KEY(new Color(255,0,220)),COIN(new Color(0,0,255)),KEY_SLOT(new Color(0,255,255)), KEY_SLOT_FILLED(new Color(0,1,2));
    MD2Model model;
    int color;
    TileType(Color color) {
        this.color = color.getRGB();
    }
    void loadModel(MD2Model model) {
        this.model = model;
    }
}
