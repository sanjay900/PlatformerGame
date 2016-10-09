import processing.sound.*;

import game.Game;
import java.util.function.*;
Game game;
SoundFile soundFile;
public void setup() {
  game.setup();
}
public void settings() {
  init();
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

public void mouseReleased() {
  game.mouseReleased();
}
public void init() {
  //The game object takes a PApplet, and then a sound consumer
  //To tell it how media files are played. This is used because
  //IntelliJ and Processing treat media files differently.
  game = new Game(this, new SoundConsumer());
}
public class SoundConsumer implements BiConsumer<Boolean, String> {
  public void accept(Boolean isMusic, String fileName) {
    //Stop the last song that was played
    if (isMusic && soundFile != null) soundFile.stop();
    //If we are playing music files, look in assets/music otherwise look in assets/sounds
    SoundFile soundFile = new SoundFile(CGRAGame.this, "assets/"+(isMusic?"music/":"sounds/")+fileName);
    if (isMusic) {
      //Loop and store the music handle somewhere
      soundFile.loop(); 
      CGRAGame.this.soundFile = soundFile;
    } else soundFile.play();
  }
}