package menu;

import game.Game;
import levels.LevelParser;
import org.json.JSONException;
import processing.core.PConstants;
import processing.opengl.PGraphics3D;
import server.ScoreObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SelectionButton {
    private LinkedHashMap<String,Button> buttonList = new LinkedHashMap<>();
    private ArrayList<String> names;
    private ArrayList<Boolean> complete = new ArrayList<>();
    private String name;
    private Game game;
    private String fileName;
    private String current;
    private ScoreObject toSend = new ScoreObject();
    public SelectionButton(File f, Game game) {
        this.game = game;
        name = f.getName();
        fileName= new File(f,"index.png").getAbsolutePath();
        File[] files = f.listFiles();
        float btWidth = game.applet.width/3;
        float btHeight = game.applet.height/10;
        Button bt;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains("index.png")) continue;
                File f2 = files[i];
                buttonList.put(f2.getName().replace(".png",""),bt = new Button(game.applet,((i-1)%3)*btWidth,150+((i-1)/3)*btHeight,btWidth,btHeight,(f2.getName().startsWith("z")?f2.getName().substring(1):f2.getName()).replace(".png","")));
                complete.add(false);
                bt.setOnMouseClicked(()->this.clickedButton(f2));
            }
        }
        names = new ArrayList<>(buttonList.keySet());
        buttonList.put("<",bt = new Button(game.applet,0,game.applet.height/2,btWidth/4,btWidth/4,"<"));
        bt.setOnMouseClicked(game::prevPack);
        buttonList.put(">",bt = new Button(game.applet,game.applet.width-btWidth/4,game.applet.height/2,btWidth/4,btWidth/4,">"));
        bt.setOnMouseClicked(game::nextPack);
        toSend.setPack(name.startsWith("z")?name.substring(1):name);
    }
    private void clickedButton(File f) {
        if (game.pauseBetween) return;
        game.pauseBetween = true;
        this.current = f.getName().replace(".png","");
        game.current = LevelParser.parseLevel(game,f);
        game.mode(Game.Mode.GAME);
        game.begin();
    }

    public void render() {
        game.applet.textSize(40);
        game.applet.text(name.startsWith("z")?name.substring(1):name,game.applet.width/2,100);
        game.applet.imageMode(PConstants.CENTER);
        ((PGraphics3D)game.applet.g).textureSampling(3);
        game.applet.image(game.applet.loadImage(fileName),game.applet.width/2,100+game.applet.height/2,game.applet.width/3,game.applet.height/3);
        ((PGraphics3D)game.applet.g).textureSampling(5);
        game.applet.imageMode(PConstants.CORNER);
        buttonList.values().forEach(Button::draw);
    }
    public boolean nextLevel() {
        int index = names.indexOf(current);
        index++;
        if (index >= names.size()) {
            game.begin();
            game.mode(Game.Mode.SELECTION);
            return false;
        }
        game.pauseBetween = false;
        this.buttonList.get(names.get(index)).run();
        return true;
    }
    public void failLevel() {
        toSend.setName((current.startsWith("z")?current.substring(1):current).replace(".png",""));
        toSend.setCoins(game.coins);
        toSend.setDeaths(game.deaths);
        toSend.setComplete(false);
        try {
            game.client.emit("scoreGot",toSend.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void completeLevel() {
        toSend.setName((current.startsWith("z")?current.substring(1):current).replace(".png",""));
        toSend.setCoins(game.coins);
        toSend.setDeaths(game.deaths);
        toSend.setComplete(true);
        try {
            game.client.emit("scoreGot",toSend.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int index = names.indexOf(current);
        complete.set(index,true);
        buttonList.values().toArray(new Button[buttonList.size()])[index].setColor(0,255,0);
    }
}