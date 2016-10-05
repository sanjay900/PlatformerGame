package game;

import MD2.Importer;
import MD2.MD2Model;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.MediaPlayer;
import levels.Map;
import lombok.Getter;
import menu.Button;
import menu.SelectionButton;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.opengl.PGraphics3D;
import tiles.TileType;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class Game implements PConstants {
    public Game(PApplet other, Consumer<String> musicResolver, Consumer<String> soundResolver) {
        this.soundResolver = soundResolver;
        this.musicResolver = musicResolver;
        ProcessingRunner.instance = other;
        applet = other;
    }
    Consumer<String> musicResolver;
    Consumer<String> soundResolver;
    public PApplet applet;
    public Socket client;
    public boolean pauseBetween = false;
    public int deaths = 0;
    public int coins = 0;
    float flagAngle = 0;
    public Mode mode = null;
    public Map current;
    public ArrayList<Player> players = new ArrayList<>();
    public Player currentPlayer;
    List<Button> buttons = new ArrayList<>();
    List<SelectionButton> packs = new ArrayList<>();
    PImage background;
    PImage backgroundIngame;
    PImage header;
    MD2Model model;
    Importer importer = new Importer();

    void settings() {
        applet.size(800, 600, P3D);
    }

    void keyPressed() {
        if (applet.key == ENTER && mode == Mode.GAME ) {
            if (!players.isEmpty()) {
                currentPlayer.stop();
                currentPlayer = players.get((players.indexOf(currentPlayer)+1)%players.size());
            }
        }
        if (applet.key == ESC) {
            nextLevel();
            applet.key = 0;
            players.forEach(Player::restart);
            deaths = 0;
            mode(Mode.MENU);
        }
        currentPlayer.keyPressed();
    }

    MediaPlayer mplayer;

    void keyReleased() {
        currentPlayer.keyReleased();
    }

    public File resolve(String fname) {
        return applet.dataFile(fname);
    }
    void setup() {
        new JFXPanel();
        applet.textFont(applet.createFont("assets/fonts/munro.ttf", 32));
        backgroundIngame = applet.loadImage("assets/textures/backgrounds/main.png");
        background = applet.loadImage("assets/textures/backgrounds/menu.png");
        header = applet.loadImage("assets/textures/banner.png");
        for (TileType type: TileType.values()) {
            type.loadModel(this);
        }
        applet.noStroke();
        Button temp;
        buttons.add(temp = new Button(applet, 250, 340, 300, 75, "Play"));
        temp.setOnMouseClicked(() -> mode(Mode.SELECTION));
        buttons.add(temp = new Button(applet, 250, 340 + 80, 300, 75, "Quit"));
        temp.setOnMouseClicked(() -> {
            if (!pauseBetween) {
                applet.exit();
            }
        });
        File[] files = resolve("levels").listFiles();
        if (files == null) {
            System.out.println("Unable to load textures!");
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                packs.add(new SelectionButton(f, this));
            }
        }
        currentPack = packs.get(0);
        try {
            client = IO.socket("http://localhost:9092");
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mode(Mode.MENU);
    }

    public void mode(Mode menu) {
        if (mode != menu && mode != Mode.SELECTION && (menu == Mode.MENU || menu == Mode.SELECTION)) {
            musicResolver.accept("Djent.mp3");
        } else if (mode != Mode.GAME && menu == Mode.GAME) {
            musicResolver.accept("fkhalkf.mp3");
        }
        /*if (mode != Mode.CREDITS && menu == Mode.CREDITS) {
            musicResolver.accept("Another Night that Fades Away (Credits).mp3");
        }*/
        this.mode = menu;
    }

    public void nextPack() {
        if (pauseBetween) return;
        pauseBetween = true;
        currentPack = packs.get((1 + packs.indexOf(currentPack)) % packs.size());
    }

    public void prevPack() {
        if (pauseBetween) return;
        pauseBetween = true;
        int index = (packs.indexOf(currentPack) - 1) % packs.size();
        if (index < 0) index = packs.size() + index;
        currentPack = packs.get(index);
    }

    void mouseReleased() {
        pauseBetween = false;
    }

    void draw() {
        if (mode == Mode.MENU) {
            drawMenu();
        } else if (mode == Mode.GAME) {
            drawGame();
        } else if (mode == Mode.SELECTION) {
            drawSelection();
        }

    }

    SelectionButton currentPack;

    private void drawSelection() {
        applet.background(background);
        currentPack.render();
    }

    private void drawMenu() {

        ((PGraphics3D) applet.g).textureSampling(3);
        players.forEach(Player::render);
        applet.background(background);
        applet.image(header, 100, 100, applet.width - 200, 200);
        buttons.forEach(Button::draw);
    }
    private void drawGame() {
        ((PGraphics3D) applet.g).textureSampling(5);
        applet.pushMatrix();
        float scrollAmt = applet.width/2 - (currentPlayer.getBounds().getX() + currentPlayer.getBounds().getWidth());
        float totalScroll = applet.width - current.platforms.length * currentPlayer.getBounds().getWidth();

        if (scrollAmt < 0) {
            applet.translate(PApplet.constrain(totalScroll, scrollAmt, 0), 0);
        }
        applet.hint(PConstants.ENABLE_DEPTH_TEST);
        applet.background(255);
        current.drawFrame();
        new ArrayList<>(players).forEach(Player::updatePosition);
        players.forEach(Player::draw);
        currentPlayer.highlight();
        applet.textSize(40);
        applet.popMatrix();
        current.drawKeys();
        applet.hint(PConstants.DISABLE_DEPTH_TEST);
        applet.fill(255);
        applet.text("DEATHS: " + deaths + "    COINS: " + coins, 250, 35);
    }

    void nextLevel() {
        currentPack.nextLevel();
    }

    public void begin() {
        players.forEach(Player::start);
    }

    public enum Mode {
        MENU, GAME, SELECTION
    }
}
