package tiles;

import game.Game;
import game.Player;
import lombok.Getter;
import net.tangentmc.collisions.Rectangle2D;

/**
 * Created by sanjay on 8/10/2016.
 */
@Getter
public class Button extends Tile {
    private static Game game;
    private boolean down = false;
    public Button(Rectangle2D bounds, TileType type, Game game) {
        super(bounds, type);
        Button.game = game;
    }
    public void reset() {
        down = false;
        this.setType(TileType.valueOf(type.name().replace("_OFF","_ON")));
        game.current.gates.forEach(g -> g.toggle(game));
    }

    public boolean collide(Tile pl) {
        this.setType(TileType.valueOf(type.name().replace("_ON","_OFF")));
        down = true;
        game.current.gates.forEach(g -> g.toggle(game));
        return false;
    }

    public String colour() {

        return type.name().replace("BUTTON_","").replace("ON_","").replace("OFF_","").replace("_CIRC","").replace("_STAR","");
    }
    public ButtonType getBtType() {
        if (type.name().contains("CIRC")) return ButtonType.CIRCLUAR;
        if (type.name().contains("STAR")) return ButtonType.STAR;
        return ButtonType.SQUARE;
    }
    public enum ButtonType {
        CIRCLUAR,SQUARE,STAR
    }
    public boolean isDown() {
        if (getBtType() == ButtonType.SQUARE) {
            return down;
        }
        return game.getPlayers().stream().anyMatch(p -> p.getBounds().intersects(getBounds()))||game.current.boxs.stream().anyMatch(p -> p.getBounds().intersects(getBounds()));
    }
}
