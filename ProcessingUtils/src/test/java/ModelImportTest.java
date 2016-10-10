import net.tangentmc.model.MD2.Animation;
import net.tangentmc.model.MD2.Importer;
import net.tangentmc.model.MD2.MD2Model;
import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;

import java.io.File;
import java.io.IOException;

/**
 * Created by sanjay on 25/08/2016.
 */
public class ModelImportTest extends PApplet {
    public static void main(String[] args) {
        ProcessingRunner.run(new ModelImportTest());
    }
    private Importer importer;
    MD2Model MD2Model;
    MD2Model MD2Model2;
    MD2Model MD2Model3;
    public void draw() {
        if (keyPressed && key == 'z') MD2Model.setAnimation(StinkyAnimations.WALKING.getAnimation(),0.1f);
        if (keyPressed && key == 'x') MD2Model.setAnimation(StinkyAnimations.PUSHING.getAnimation(),0.1f);
        clear();
        background(255);
        pushMatrix();
        translate(100,200);
        rotate(HALF_PI,1,0,0);
        rotate(HALF_PI,0,0,1);
        MD2Model.drawModel();
        popMatrix();

        pushMatrix();
        translate(300,200);
        rotate(HALF_PI,1,0,0);
        rotate(HALF_PI,0,0,1);
        MD2Model2.drawModel();
        popMatrix();

        pushMatrix();
        translate(300,400);
        rotate(HALF_PI,1,0,0);
        rotate(HALF_PI,0,0,1);
        MD2Model3.drawModel();
        popMatrix();
    }
    public void setup() {
        noStroke();
        try {
            importer=new Importer();
            MD2Model = importer.importModel(new File("stinky.md2"),loadImage("File0165.png"),this);
            MD2Model2 = importer.importModel(new File("bomb.md2"),loadImage("File0020.png"),this);
            MD2Model3 = importer.importModel(new File("block.md2"),loadImage("block.png"),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MD2Model.setAnimation(StinkyAnimations.WALKING.getAnimation(),0.1f);
        MD2Model2.setAnimation(new Animation(24,0,100,1),0.1f);
        MD2Model3.setAnimation(new Animation(1,0,100,1),0.1f);
    }
    public void settings() {
        size(600,600,P3D);
    }

}
