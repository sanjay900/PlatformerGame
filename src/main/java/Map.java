import processing.core.PApplet;
import processing.core.PShape;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by sanjay on 26/08/2016.
 */
public class Map {
    Game game;
    Shape[][] platforms;
    private static final char player = '?';
    private static final char spike = '/';
    private static final char platform = '#';
    private static final char breakable = '=';
    private static final char goal = '$';
    float squareWidth = 10;
    float squareHeight = 10;
    public Map(File f, Game game) {
        this.game = game;
        try {
            List<String> map = Files.readAllLines(f.toPath());
            char[][] platforms = new char[map.size()][map.get(0).length()];
            squareHeight = game.height/24;
            squareWidth = game.width/32;
            for (int i = 0; i < map.size(); i++) {
                platforms[i] = map.get(i).toCharArray();
                int pos = map.get(i).indexOf(player);
                if (pos >=0) {
                    game.player = new Player(pos*squareWidth,i*squareHeight,game);
                }
            }
            for (int y = 0; y < map.size(); y++) {
                for (int x = 0; x < map.get(y).length(); x+=2) {
                    int test = platforms[y][x]-'0';
                    game.fill(255,255,255);
                    if (test >= 0 && test <= 9 || platforms[y][x] == platform) {
                        game.fill(0);
                    }
                    if (platforms[y][x] == spike) {
                        game.fill(100,100,100);
                    }
                    if (platforms[y][x] == breakable) {
                        game.fill(0,100,0);
                    }
                    if (platforms[y][x] == goal) {
                        game.fill(255,255,0);
                    }
                    if (platforms[y][x] == player) {
                        game.fill(255,255,0);
                    }
                    game.rect((x/2)*squareWidth,y*squareHeight,squareWidth, squareHeight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public void drawFrame() {
        for (int y = 0; y < platforms.length; y++) {
            for (int x = 0; x < platforms[y].length; x+=2) {
                int test = platforms[y][x]-'0';
                game.fill(255,255,255);
                if (test >= 0 && test <= 9 || platforms[y][x] == platform) {
                    game.fill(0);
                }
                if (platforms[y][x] == spike) {
                    game.fill(100,100,100);
                }
                if (platforms[y][x] == breakable) {
                    game.fill(0,100,0);
                }
                if (platforms[y][x] == goal) {
                    game.fill(255,255,0);
                }
                game.rect((x/2)*squareWidth,y*squareHeight,squareWidth, squareHeight);
            }
        }
    }
}
