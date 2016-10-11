package game;

import MD2.Importer;
import MD2.MD2Model;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.scene.media.MediaPlayer;
import levels.Map;
import lombok.Getter;
import menu.Button;
import menu.SelectionButton;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphics3D;
import tiles.TileType;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

//This isnt useful since we access from processing which is a different package.
@SuppressWarnings("WeakerAccess")
@Getter
public class Game implements PConstants {
    public Game(PApplet other, BiConsumer<Boolean,String> soundResolver) {
        this.soundResolver = soundResolver;
        ProcessingRunner.instance = other;
        applet = other;
    }
    //Sound resolver for both processing and intelliJ, as they handle sound differently.
    BiConsumer<Boolean, String> soundResolver;
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

    public void settings() {
        applet.size(800, 600, P3D);
        applet.fullScreen();
    }

    public void keyPressed() {
        if (mode == Mode.MENU) return;
        if (applet.key == ENTER && mode == Mode.GAME ) {
            if (!players.isEmpty()) {
                currentPlayer.stop();
                if (players.stream().allMatch(Player::hasWon)) return;
                //Find the first player that hasnt won.
                do {
                    currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
                } while(currentPlayer.won);
            }
        }
        if (applet.key == ESC) {
            nextLevel();
            applet.key = 0;
            players.forEach(Player::restart);
            deaths = 0;
            mode(Mode.MENU);
        }
        if (!players.isEmpty()) {
            currentPlayer.keyPressed();
        }
    }


    MediaPlayer mplayer;

    public void keyReleased() {
        if (currentPlayer != null)
        currentPlayer.keyReleased();
    }
    //Resolve a data file from the data folder
    public File resolve(String fname) {
        return applet.dataFile(fname);
    }
    Button ret;
    public void setup() {
        applet.textFont(applet.createFont("assets/fonts/munro.ttf", 32));
        backgroundIngame = applet.loadImage("assets/textures/backgrounds/backdrop1.png");
        background = applet.loadImage("assets/textures/backgrounds/menu.png");
        header = applet.loadImage("assets/textures/banner.png");
        for (TileType type: TileType.values()) {
            type.loadModel(this);
        }
        applet.noStroke();
        //Create buttons
        Button temp;
        buttons.add(temp = new Button(applet, (applet.width-300)/2, applet.height/3+applet.height/8+ applet.height/8, 300, applet.height/10, "Play"));
        temp.setOnMouseClicked(() -> mode(Mode.SELECTION));
        buttons.add(temp = new Button(applet, (applet.width-300)/2, applet.height/3+applet.height/8 + applet.height/8+ applet.height/8, 300, applet.height/10, "Instructions"));
        temp.setOnMouseClicked(() -> mode = Mode.INST);
        buttons.add(temp = new Button(applet, (applet.width-300)/2, applet.height/3+applet.height/8+applet.height/8 + applet.height/8+ applet.height/8, 300, applet.height/10, "Quit"));
        temp.setOnMouseClicked(() -> applet.exit());
        ret = new Button(applet, applet.width/30, applet.height - applet.height/8, 300, applet.height/10, "Back");
        ret.setOnMouseClicked(() -> mode = Mode.MENU);
        File[] files = resolve("levels").listFiles();
        if (files == null) {
            System.out.println("Unable to load levels!");
            return;
        }
        //Create level packs based on the levels
        for (File f : files) {
            if (f.isDirectory()) {
                packs.add(new SelectionButton(f, this));
            }
        }
        currentPack = packs.get(0);
        //Start a websocket server that keeps track of deaths
        try {
            client = IO.socket("http://localhost:9092");
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mode(Mode.MENU);
        backgroundIngame.resize(applet.width, applet.height);
        background.resize(applet.width, applet.height);
        //With processing, its faster to texture a plane and draw that then use background.
        //Im assuming this is because of some technicalities with OPENGL
        //TODO: Should really consider making this a helper function in ProcessingUtils
        backgroundShape = applet.createShape();
        backgroundShape.beginShape(PConstants.QUAD);
        backgroundShape.texture(backgroundIngame);
        backgroundShape.textureMode(PConstants.NORMAL);
        backgroundShape.vertex(0,0,0,0);
        backgroundShape.vertex(applet.width,0,1,0);
        backgroundShape.vertex(applet.width,applet.height,1,1);
        backgroundShape.vertex(0,applet.height,0,1);
        backgroundShape.endShape();
    }
    PShape backgroundShape;
    public void mode(Mode menu) {
        if (mode != menu && mode != Mode.SELECTION && (menu == Mode.MENU || menu == Mode.SELECTION)) {
            soundResolver.accept(true,"Djent.mp3");
        } else if (mode != Mode.GAME && menu == Mode.GAME) {
            soundResolver.accept(true,"fkhalkf.mp3");
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

    public void mouseReleased() {
        pauseBetween = false;
    }

    public void draw() {
        if (mode == Mode.MENU) {
            drawMenu();
        } else if (mode == Mode.GAME) {
            drawGame();
        } else if (mode == Mode.SELECTION) {
            drawSelection();
        } else {
            drawInstructions();
        }

    }

    private void drawInstructions() {
        applet.shape(backgroundShape);
        applet.fill(0,0,0,128);
        applet.rect(30,30,applet.width-60,applet.height-200);
        applet.fill(255,255,255);
        applet.text("Welcome to Bob's Adventure!\n"+
                "This game is a simple platformer with a twist.\n" +
                "By holding the [JUMP] key, you can stick to the ceiling.\n"+
                "By using [ENTER], you can switch between controlling\n"+
                "different characters, and your goal is to get them all to\n"+
                "the end flag. You can open gates by standing on their\n"+
                "respective buttons, and you can push boxes onto buttons.\n"+
                "star teleporter pads will teleport the player between them.\n"+
                "Note: You can restart a level with [R]."
                ,applet.width/2,90);
        ret.draw();
    }

    SelectionButton currentPack;

    private void drawSelection() {
        applet.shape(backgroundShape);
        currentPack.render();
    }

    private void drawMenu() {
        //Pixelate images instead of interpolating
        ((PGraphics3D) applet.g).textureSampling(3);
        players.forEach(Player::render);
        applet.clear();
        applet.shape(backgroundShape);
        applet.image(header, 100, 100, applet.width - 200, applet.height/2.5f);
        buttons.forEach(Button::draw);
    }
    boolean texInit = false;
    private void drawGame() {
        ((PGraphics3D) applet.g).textureSampling(3);
        applet.clear();
        applet.shape(backgroundShape);
        float maxWidth = current.xLength * current.playerStart.getBounds().getWidth();
        applet.pushMatrix();
        float scroll = currentPlayer.getBounds().getX();
        scroll = Math.max(applet.width/2,scroll);
        if (scroll < maxWidth-applet.width/2) {
            scroll-=applet.width/2;
        } else {
            scroll = maxWidth-applet.width;
        }
        if (maxWidth < applet.width) {
            scroll = currentPlayer.getBounds().getX()-applet.width/2;
        }
        applet.translate(-scroll, 0);
        //draw 3d
        applet.hint(PConstants.ENABLE_DEPTH_TEST);
        current.drawFrame();
        new ArrayList<>(players).forEach(Player::updatePosition);
        players.forEach(Player::draw);
        currentPlayer.highlight();
        applet.textSize(40);
        applet.popMatrix();
        applet.fill(0,0,0,128);
        applet.rect(0,0,applet.width,applet.height/9);
        current.drawKeys();
        //2d objects dont have a z axis, so don't test it
        applet.hint(PConstants.DISABLE_DEPTH_TEST);
        applet.fill(255);
        applet.text("DEATHS: " + deaths + "    COINS: " + coins, 250, (applet.height/15));
        applet.noStroke();
    }

    void nextLevel() {
        currentPack.nextLevel();
    }

    public void begin() {
        players.forEach(Player::start);
    }

    public enum Mode {
        MENU, GAME, SELECTION, INST
    }
}
