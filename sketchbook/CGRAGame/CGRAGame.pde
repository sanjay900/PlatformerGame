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
      //The game object takes a PApplet, and then a couple of consumers
      //To tell it how media files are played. This is used because
      //IntelliJ and Processing treat media files differently.
     game = new Game(this,new MusicConsumer(), new EffectConsumer()); 
    }
    public class MusicConsumer implements Consumer<String> {
      public void accept(String fileName) {
          if (soundFile != null) soundFile.stop();
          soundFile = new SoundFile(CGRAGame.this, fileName);
              soundFile.loop(); 
        }
    }
    //Consumer that plays sound effects when given a string based filename
    public class EffectConsumer implements Consumer<String> {
      public void accept(String fileName) {
           new SoundFile(CGRAGame.this, "assets/"+fileName).play();
         }
    }