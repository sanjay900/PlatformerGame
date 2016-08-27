package menu;

import game.Game;
import levels.LevelParser;
import levels.Map;
import processing.core.PConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sanjay on 28/08/2016.
 */
public class SelectionButton {
    ArrayList<Button> buttonList = new ArrayList<>();
    String name;
    Game game;
    String fileName;
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
                buttonList.add(bt = new Button(game,((i-1)%3)*btWidth,150+((i-1)/3)*50,btWidth,50,files[i].getName()));
                File f2 = files[i];
                bt.setOnMouseClicked(()->this.clickedButton(f2));
            }
        }
        buttonList.add(bt = new Button(game,0,game.height/2,btWidth/4,btWidth/4,"<"));
        bt.setOnMouseClicked(game::prevPack);
        buttonList.add(bt = new Button(game,game.width-btWidth/4,game.height/2,btWidth/4,btWidth/4,">"));
        bt.setOnMouseClicked(game::nextPack);
    }

    private void clickedButton(File f) {
        if (game.pauseBetween) return;
        game.pauseBetween = true;
        if (f.getName().contains("index")) return;
        game.current = LevelParser.parseLevel(game,f);
        Map.levelNum = getNumber(f.getName());
        game.mode = Game.Mode.GAME;
    }

    public void render() {
        game.textSize(40);
        game.text(name,400,100);
        game.imageMode(PConstants.CENTER);
        game.image(game.loadImage(fileName),400,100+game.height/2,500,game.height/3);
        buttonList.forEach(Button::draw);
    }
    public int getNumber(String name) {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(name);
        if(m.find()) {
            String result = m.group();
            return Integer.valueOf(result);
        }
        return 0;
    }
}
