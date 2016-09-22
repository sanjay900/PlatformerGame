package game;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import levels.Map;
import menu.Button;
import menu.SelectionButton;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.opengl.PGraphics3D;
import tiles.TileType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class Game implements PConstants {
    public Game(PApplet other, BiConsumer<String,Boolean> playSound, Consumer<String> playSoundStr) {
        this.playSoundStr = playSoundStr;
        this.playSound = playSound;
        ProcessingRunner.instance = other;
        applet = other;
    }
    BiConsumer<String,Boolean> playSound;
    Consumer<String> playSoundStr;
    public PApplet applet;
    public Socket client;
    public boolean pauseBetween = false;
    public int deaths = 0;
    public int coins = 0;
    float flagAngle = 0;
    public Mode mode = null;
    public Map current;
    public Player player;
    List<Button> buttons = new ArrayList<>();
    List<SelectionButton> packs = new ArrayList<>();
    PImage background;
    PImage backgroundIngame;
    PImage header;
    PImage pauseScreen;
    MD2Model model;
    Importer importer = new Importer();

    public void settings() {
        applet.size(800, 600, P3D);
    }

    public void keyPressed() {
        if (applet.key == ENTER && mode == Mode.MENU) {
            nextLevel();
            applet.key = 0;
            player.restart();
            deaths = 0;
        }
        if (applet.key == ESC) {
            nextLevel();
            applet.key = 0;
            player.restart();
            deaths = 0;
            mode(Mode.MENU);
        }
        player.keyPressed();
    }

    MediaPlayer mplayer;

    public void keyReleased() {
        player.keyReleased();
    }

    public File resolve(String fname) {
        return applet.dataFile(fname);
    }
    public void setup() {
        new JFXPanel();
        applet.textFont(applet.createFont("assets/munro.ttf", 32));
        player = new Player(0, 0, this);
        backgroundIngame = applet.loadImage("assets/BACK.png");
        background = applet.loadImage("assets/menuwood.png");
        header = applet.loadImage("assets/TEMP BANNER.png");
        pauseScreen = applet.loadImage("assets/pause.png");
        try {
            model = importer.importModel(resolve("assets/models/block.md2"),applet.loadImage("assets/models/block.png"), applet);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.BLOCK.loadModel(model);

            model = importer.importModel(resolve("assets/models/keyholefill.md2"),applet.loadImage("assets/models/KEYholefill.png"), applet);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.KEY_SLOT_FILLED.loadModel(model);

            model = importer.importModel(resolve("assets/models/keyhole.md2"),applet.loadImage("assets/models/KEYhole.png"), applet);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.KEY_SLOT.loadModel(model);

            model = importer.importModel(resolve("assets/models/brokern.md2"),applet.loadImage("assets/models/brokern.png"), applet);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.BREAKABLE.loadModel(model);

            model = importer.importModel(resolve("assets/models/dapokiy.obj.md2"),applet.loadImage("assets/models/dapokiy.png"), applet);
            TileType.UPSIDE_DOWN_SPIKE.loadModel(model);
            TileType.LEFT_SPIKE.loadModel(model);
            TileType.RIGHT_SPIKE.loadModel(model);
            TileType.SPIKE.loadModel(model);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);

            model = importer.importModel(resolve("assets/models/flag.md2"),applet.loadImage("assets/models/FLAG.png"), applet);
            TileType.EXIT.loadModel(model);
            model.setAnimation(new Animation(1, 0, 2f, 0.1f), 2f);

            model = importer.importModel(resolve("assets/models/key.md2"),applet.loadImage("assets/models/KEY.png"), applet);
            TileType.KEY.loadModel(model);
            model.setAnimation(new Animation(1, 0, 0.2f, 0.1f), 2f);

            model = importer.importModel(resolve("assets/models/coin.md2"),applet.loadImage("assets/models/COIN.png"), applet);
            TileType.COIN.loadModel(model);
            model.setAnimation(new Animation(1, 0, 0.2f, 0.1f), 2f);

        } catch (IOException e) {
            e.printStackTrace();
        }
        applet.noStroke();
        player.readImages(this);
        player.model.setAnimation(AnimationCycles.WALKING.getAnimation(), 2f);
        Button temp;
        buttons.add(temp = new Button(applet, 250, 340, 300, 75, "Play"));
        temp.setOnMouseClicked(() -> mode(Mode.SELECTION));
        buttons.add(temp = new Button(applet, 250, 340 + 80, 300, 75, "Credits"));
        temp.setOnMouseClicked(() -> mode(Mode.CREDITS));
        buttons.add(temp = new Button(applet, 250, 340 + 170, 300, 75, "Quit"));
        temp.setOnMouseClicked(() -> {
            if (!pauseBetween) {
                applet.exit();
            }
        });
        File[] files = resolve("levels").listFiles();
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
        loadModels();
        mode(Mode.MENU);
    }

    public void mode(Mode menu) {
        if (mode != menu && mode != Mode.SELECTION && (menu == Mode.MENU || menu == Mode.SELECTION)) {
            playSound.accept("Djent.mp3", true);
        } else if (mode != Mode.GAME && menu == Mode.GAME) {
            playSound.accept("fkhalkf.mp3", true);
        }if (mode != Mode.CREDITS && menu == Mode.CREDITS) {
            playSound.accept("Another Night that Fades Away (Credits).mp3", false);
        }
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
        } else if (mode == Mode.CREDITS) {
            drawCredits();
        }

    }

    SelectionButton currentPack;

    private void drawSelection() {
        applet.background(background);
        currentPack.render();
    }

    private void drawMenu() {

        ((PGraphics3D) applet.g).textureSampling(3);
        player.model.drawModel();
        applet.background(background);
        applet.image(header, 100, 100, applet.width - 200, 200);
        buttons.forEach(Button::draw);
    }

    private void drawGame() {
        ((PGraphics3D) applet.g).textureSampling(5);
        applet.pushMatrix();
        float scrollAmt = (float) (400 - (player.getBounds().getX() + player.getBounds().getWidth()));
        float totalScroll = (float) (800 - current.platforms.length * player.getBounds().getWidth());

        if (scrollAmt < 0) {
            applet.translate(PApplet.constrain(totalScroll, scrollAmt, 0), 0);
        }
        applet.hint(PConstants.ENABLE_DEPTH_TEST);
        applet.background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
        applet.textSize(40);
        applet.popMatrix();
        current.drawKeys();
        applet.hint(PConstants.DISABLE_DEPTH_TEST);
        applet.fill(255);
        applet.text("DEATHS: " + deaths + "    COINS: " + coins, 250, 35);
    }

    public void nextLevel() {
        currentPack.nextLevel();
    }
    MD2Model[] credits = new MD2Model[4];
    int currentCredit = 0;
    String[] text = {"Sanjay Govind - Head Code, manager","Jacob Cohn-Gell - Other coder, map creations","Jesse Walls - Graphic Designer, Map Creator, 3d Models","Grayden Tavendale - Music / Sound Design", "This game was made by Tangent Games 2016", "In Memory of Harambe - You Will Be Forever With Us"};
    float currentInterp = 0;
    long lastDist = -1;
    private void drawCredits() {
        if (currentCredit > 3) {
            mode(Mode.MENU);
            mplayer.stop();
            return;
        }
        if (lastDist != -1 && applet.frameCount- lastDist < applet.frameRate) {
            currentInterp = 1;
        }
        applet.clear();
        applet.pushMatrix();
        applet.fill(255);
        applet.text(text[currentCredit],400,applet.lerp(0,200,currentInterp));
        applet.translate(400,applet.height-applet.lerp(0,200,currentInterp),-50);
        applet.scale(5);
        applet.rotateX(HALF_PI);
        applet.rotateZ(-HALF_PI);
        credits[currentCredit].drawModel();
        applet.popMatrix();
        if (lastDist != -1 && applet.frameCount- lastDist < applet.frameRate *5) {
            return;
        }
        if (lastDist != -1) {
            lastDist = -1;
            currentCredit++;
            currentInterp = 0;
            return;
        }
        currentInterp+=0.003;
        if (currentInterp >= 1) {
            currentInterp = 0;
            lastDist = applet.frameCount;
        }
    }
    private void loadModels() {
        try {
            credits[0] = importer.importModel(resolve("assets/models/bob.md2"),applet.loadImage("assets/character/sanjay.png"),applet);
            credits[1] = importer.importModel(resolve("assets/models/bob.md2"),applet.loadImage("assets/character/jacob.png"),applet);
            credits[2] = importer.importModel(resolve("assets/models/bob.md2"),applet.loadImage("assets/character/Jesse.png"),applet);
            credits[3] = importer.importModel(resolve("assets/models/bob.md2"),applet.loadImage("assets/character/grayden.png"),applet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public enum Mode {
        MENU, GAME, SELECTION, CREDITS
    }
}
