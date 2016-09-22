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
        game = new Game(this,this::playSound,this::playSound);
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

    public void playSound(String filename) {
        final Media media = new Media(game.resolve("assets/"+filename).toURI().toString());
        MediaPlayer mplayer = new MediaPlayer(media);
        mplayer.play();
    }
    private MediaPlayer mplayer;
    public void playSound(String fileName, boolean infinite) {
        if (mplayer != null) mplayer.stop();
        final Media media = new Media(game.resolve("assets/"+fileName).toURI().toString());
        mplayer = new MediaPlayer(media);
        mplayer.setOnEndOfMedia(()->{
            mplayer.seek(Duration.ZERO);
        });
        mplayer.play();
    }
}
