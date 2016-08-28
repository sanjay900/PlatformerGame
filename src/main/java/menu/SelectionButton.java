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

/**
 * Created by sanjay on 28/08/2016.
 */
public class SelectionButton {
    LinkedHashMap<String,Button> buttonList = new LinkedHashMap<>();
    ArrayList<String> names;
    ArrayList<Boolean> complete = new ArrayList<>();
    String name;
    Game game;
    String fileName;
    String current;
    ScoreObject toSend = new ScoreObject();
    public SelectionButton(File f, Game game) {
        this.game = game;
        name = f.getName();
        fileName= new File(f,"index.png").getAbsolutePath();
        File[] files = f.listFiles();
        float btWidth = 800/3;
        Button bt;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains("index.png")) continue;
                File f2 = files[i];
                buttonList.put(f2.getName().replace(".png",""),bt = new Button(game,((i-1)%3)*btWidth,150+((i-1)/3)*50,btWidth,50,(f2.getName().startsWith("z")?f2.getName().substring(1):f2.getName()).replace(".png","")));
                complete.add(false);
                bt.setOnMouseClicked(()->this.clickedButton(f2));
            }
        }
        names = new ArrayList<>(buttonList.keySet());
        buttonList.put("<",bt = new Button(game,0,game.height/2,btWidth/4,btWidth/4,"<"));
        bt.setOnMouseClicked(game::prevPack);
        buttonList.put(">",bt = new Button(game,game.width-btWidth/4,game.height/2,btWidth/4,btWidth/4,">"));
        bt.setOnMouseClicked(game::nextPack);
        toSend.setPack(name.startsWith("z")?name.substring(1):name);
    }
    private void clickedButton(File f) {
        if (game.pauseBetween) return;
        game.pauseBetween = true;
        this.current = f.getName().replace(".png","");
        game.current = LevelParser.parseLevel(game,f);
        game.mode = Game.Mode.GAME;
        game.player.start();
    }

    public void render() {
        game.textSize(40);
        game.text(name.startsWith("z")?name.substring(1):name,400,100);
        game.imageMode(PConstants.CENTER);
        ((PGraphics3D)game.g).textureSampling(3);
        game.image(game.loadImage(fileName),400,100+game.height/2,500,game.height/3);
        ((PGraphics3D)game.g).textureSampling(5);
        game.imageMode(PConstants.CORNER);
        buttonList.values().forEach(Button::draw);
    }
    public boolean nextLevel() {
        int index = names.indexOf(current);
        index++;
        if (index >= names.size()) {
            game.player.start();
            game.mode = Game.Mode.SELECTION;
            return false;
        }
        game.pauseBetween = false;
        this.buttonList.get(names.get(index)).run();
        return true;
    }

    public void completeLevel() {
        toSend.setName((current.startsWith("z")?current.substring(1):current).replace(".png",""));
        toSend.setCoins(game.coins);
        toSend.setDeaths(game.deaths);
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
