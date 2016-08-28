package game;

import MD2.Animation;
import MD2.Importer;
import MD2.MD2Model;
import com.sanjay900.ProcessingRunner;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import levels.Map;
import menu.Button;
import menu.SelectionButton;
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

/**
 * Created by sanjay on 26/08/2016.
 */
public class Game extends PApplet {
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

    public static void main(String[] args) {
        ProcessingRunner.run(new Game());
    }

    public void settings() {
        size(800, 600, P3D);
    }

    public void keyPressed() {
        if (key == ENTER && mode == Mode.MENU) {
            nextLevel();
            key = 0;
            player.restart();
            deaths = 0;
        }
        if (key == ESC) {
            nextLevel();
            key = 0;
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

    public void playSound(File f, boolean infinite) {
        if (mplayer != null) mplayer.stop();
        final Media media = new Media(f.toURI().toString());
        mplayer = new MediaPlayer(media);
            mplayer.setOnEndOfMedia(()->{
                mplayer.seek(Duration.ZERO);
            });
        mplayer.play();
    }

    public void setup() {
        new JFXPanel();
        textFont(createFont("assets/munro.ttf", 32));
        player = new Player(0, 0, this);
        backgroundIngame = loadImage("assets/BACK.png");
        background = loadImage("assets/menuwood.png");
        header = loadImage("assets/TEMP BANNER.png");
        pauseScreen = loadImage("assets/pause.png");
        try {
            model = importer.importModel(new File("assets/models/block.md2"), loadImage("assets/models/block.png"), this);

            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.BLOCK.loadModel(model);
            model = importer.importModel(new File("assets/models/keyholefill.md2"), loadImage("assets/models/KEYholefill.png"), this);

            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.KEY_SLOT_FILLED.loadModel(model);
            model = importer.importModel(new File("assets/models/keyhole.md2"), loadImage("assets/models/KEYhole.png"), this);

            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.KEY_SLOT.loadModel(model);
            model = importer.importModel(new File("assets/models/brokern.md2"), loadImage("assets/models/brokern.png"), this);

            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);
            TileType.BREAKABLE.loadModel(model);
            model = importer.importModel(new File("assets/models/dapokiy.obj.md2"), loadImage("assets/models/dapokiy.png"), this);

            TileType.UPSIDE_DOWN_SPIKE.loadModel(model);
            TileType.LEFT_SPIKE.loadModel(model);
            TileType.RIGHT_SPIKE.loadModel(model);
            TileType.SPIKE.loadModel(model);
            model.setAnimation(new Animation(1, 0, 1, 0.1f), 2f);

            model = importer.importModel(new File("assets/models/flag.md2"), loadImage("assets/models/FLAG.png"), this);
            TileType.EXIT.loadModel(model);
            model.setAnimation(new Animation(1, 0, 2f, 0.1f), 2f);

            model = importer.importModel(new File("assets/models/key.md2"), loadImage("assets/models/KEY.png"), this);
            TileType.KEY.loadModel(model);
            model.setAnimation(new Animation(1, 0, 0.2f, 0.1f), 2f);

            model = importer.importModel(new File("assets/models/coin.md2"), loadImage("assets/models/COIN.png"), this);
            TileType.COIN.loadModel(model);
            model.setAnimation(new Animation(1, 0, 0.2f, 0.1f), 2f);

        } catch (IOException e) {
            e.printStackTrace();
        }
        noStroke();
        player.readImages(this);
        player.model.setAnimation(AnimationCycles.WALKING.getAnimation(), 2f);
        Button temp;
        buttons.add(temp = new Button(this, 250, 340, 300, 75, "Play"));
        temp.setOnMouseClicked(() -> mode(Mode.SELECTION));
        buttons.add(temp = new Button(this, 250, 340 + 80, 300, 75, "Credits"));
        temp.setOnMouseClicked(() -> mode(Mode.CREDITS));
        buttons.add(temp = new Button(this, 250, 340 + 170, 300, 75, "Quit"));
        temp.setOnMouseClicked(() -> {
            if (!pauseBetween) {
                exit();
            }
        });
        File[] files = new File("levels").listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                packs.add(new SelectionButton(f, this));
            }
        }
        currentPack = packs.get(0);
        try {
            client = IO.socket("http://10.73.10.176:9092");
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        loadModels();
        mode(Mode.MENU);
    }

    public void mode(Mode menu) {
        if (mode != menu && mode != Mode.SELECTION && (menu == Mode.MENU || menu == Mode.SELECTION)) {
            playSound(new File("assets/Djent.mp3"), true);
        } else if (mode != Mode.GAME && menu == Mode.GAME) {
            playSound(new File("assets/fkhalkf.mp3"), true);
        }if (mode != Mode.CREDITS && menu == Mode.CREDITS) {
            playSound(new File("assets/Another Night that Fades Away (Credits).mp3"), false);
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
        background(background);
        currentPack.render();
    }

    private void drawMenu() {

        ((PGraphics3D) g).textureSampling(3);
        player.model.drawModel();
        background(background);
        image(header, 100, 100, width - 200, 200);
        buttons.forEach(Button::draw);
    }

    private void drawGame() {
        ((PGraphics3D) g).textureSampling(5);
        pushMatrix();
        float scrollAmt = (float) (400 - (player.getBounds().getX() + player.getBounds().getWidth()));
        float totalScroll = (float) (800 - current.platforms.length * player.getBounds().getWidth());

        if (scrollAmt < 0) {
            translate(constrain(totalScroll, scrollAmt, 0), 0);
        }
        hint(PConstants.ENABLE_DEPTH_TEST);
        background(255);
        current.drawFrame();
        player.updatePosition();
        player.draw();
        textSize(40);
        popMatrix();
        current.drawKeys();
        hint(PConstants.DISABLE_DEPTH_TEST);
        fill(255);
        text("DEATHS: " + deaths + "    COINS: " + coins, 250, 35);
        if (player.dontMove) image(pauseScreen, 0, 0, width, height);
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
        if (lastDist != -1 && frameCount- lastDist < frameRate) {
            currentInterp = 1;
        }
        clear();
        pushMatrix();
        fill(255);
        text(text[currentCredit],400,lerp(0,200,currentInterp));
        translate(400,height-lerp(0,200,currentInterp),-50);
        scale(5);
        rotateX(HALF_PI);
        rotateZ(-HALF_PI);
        credits[currentCredit].drawModel();
        popMatrix();
        if (lastDist != -1 && frameCount- lastDist < frameRate *5) {
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
            lastDist = frameCount;
        }
    }
    private void loadModels() {
        try {
            credits[0] = importer.importModel(new File("assets/models/bob.md2"),loadImage("assets/character/sanjay.png"),this);
            credits[1] = importer.importModel(new File("assets/models/bob.md2"),loadImage("assets/character/jacob.png"),this);
            credits[2] = importer.importModel(new File("assets/models/bob.md2"),loadImage("assets/character/Jesse.png"),this);
            credits[3] = importer.importModel(new File("assets/models/bob.md2"),loadImage("assets/character/grayden.png"),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public enum Mode {
        MENU, GAME, SELECTION, CREDITS
    }
}
