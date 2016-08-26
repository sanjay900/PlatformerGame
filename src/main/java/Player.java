import processing.core.PVector;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Player {
    Game game;
    PVector position;
    int playerWidth;
    int playerHeight;
    public Player(float x, float y, Game game) {
        playerHeight = 2*(game.height/24);
        playerWidth = game.width/32;
        position = new PVector(x,y);
        this.game = game;
    }
    public void updatePosition() {

    }
    public void draw() {
        game.fill(255,0,0);
        game.rect(position.x,position.y,playerWidth,playerHeight);
    }
}
