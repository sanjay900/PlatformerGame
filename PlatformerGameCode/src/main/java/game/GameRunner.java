package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;

/**
 * Created by sanjay on 19/09/2016.
 */
public class GameRunner extends PApplet {
    Game game;
    public static void main(String[] args) {
        ProcessingRunner.run(new GameRunner());
    }
    public GameRunner() {
        game = new Game(this,this::playSound);
    }
    public void setup() {
        game.setup();
    }
    public void settings() {
        game.settings();
    }
    public void draw() {
        game.draw();
    }
    public void keyPressed() {
        game.keyPressed();
    }
    public void keyReleased() {
        game.keyReleased();
    }
    public void mouseReleased() {game.mouseReleased();}

    public void playSound(boolean loop, String filename) {
        final Media media = new Media(game.resolve("assets/"+(loop?"music/":"sounds/")+filename).toURI().toString());
        if (loop && mplayer != null) mplayer.stop();
        mplayer = new MediaPlayer(media);
        if (loop) {
            mplayer.setOnEndOfMedia(() -> mplayer.seek(Duration.ZERO));
        }
        //mplayer.play();
    }
    private MediaPlayer mplayer;
}
