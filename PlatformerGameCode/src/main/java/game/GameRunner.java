package game;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;

/**
 * The runner for IntelliJ
 */
public class GameRunner extends PApplet {
    private Game game;
    public static void main(String[] args) {
        ProcessingRunner.run(new GameRunner());
    }
    private GameRunner() {
        new JFXPanel();
        game = new Game(this,this::playSound);
    }
    //Forward events
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
    //Play sound using javax
    private void playSound(boolean loop, String filename) {
        final Media media = new Media(game.resolve("assets/"+(loop?"music/":"sounds/")+filename).toURI().toString());
        if (loop && mplayer != null) mplayer.stop();
        MediaPlayer mplayer;
        mplayer = new MediaPlayer(media);
        if (loop) {
            mplayer.setOnEndOfMedia(() -> mplayer.seek(Duration.ZERO));
            this.mplayer = mplayer;
        }
        mplayer.play();
    }
    private MediaPlayer mplayer;
}
