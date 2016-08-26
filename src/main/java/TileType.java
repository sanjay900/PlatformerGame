import MD2.MD2Model;

import java.awt.*;

public enum TileType {
    BLOCK("assets/tiles/block.png", Color.BLACK),SPIKE("assets/tiles/spike.png", Color.RED), EXIT("assets/tiles/exit.png", Color.YELLOW),
    UPSIDE_DOWN_SPIKE("assets/tiles/spike_upside_down.png", new Color(127, 0, 0)), BREAKABLE("assets/tiles/breakable_breaking.png", Color.GRAY);
    MD2Model model;
    String fileName;
    int color;
    TileType(String fileName, Color color) {
        this.fileName = fileName;
        this.color = color.getRGB();
    }
    void loadModel(MD2Model model) {
        this.model = model;
    }
}
