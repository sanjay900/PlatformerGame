package net.tangentmc.model.obj;


import net.tangentmc.processing.ProcessingRunner;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import static processing.core.PApplet.*;
/**
 * Created by sanjay on 6/08/16.
 */

/**
 * Processing can't handle rendering complex models with children when they are imported normally.
 * To avoid this, we flatten them down into two models, and they render without losing frames.
 */
public class OBJModel {
    private PApplet __instance = ProcessingRunner.instance;
    private PShape tri;
    private PShape quad;

    /**
     * Create an OBJModel from another OBJModel
     * @param orig the original model to clone
     * @param tex the new texture to use
     */
    public OBJModel(OBJModel orig, PImage tex) {
        fromSimplified(orig,tex);
    }

    /**
     * Create an OBJModel from an existing PShape
     * @param original the original model to base upon
     * @param texture the texture to use
     */
    public OBJModel(PShape original, PImage texture) {
        simplifyShape(original,texture);
    }
    private void fromSimplified(OBJModel simplified, PImage tex) {
        tri = __instance.createShape();
        quad = __instance.createShape();
        tri.beginShape(TRIANGLES);
        tri.noStroke();
        tri.texture(tex);
        tri.textureMode(NORMAL);
        quad.beginShape(QUADS);
        quad.noStroke();
        quad.texture(tex);
        quad.textureMode(NORMAL);
        fillInShape(tri,simplified.tri);
        fillInShape(quad,simplified.quad);
        tri.endShape();
        quad.endShape();
    }
    private void simplifyShape(PShape r, PImage tex) {
        tri = __instance.createShape();
        quad = __instance.createShape();
        tri.beginShape(TRIANGLES);
        tri.noStroke();
        tri.texture(tex);
        tri.textureMode(NORMAL);
        quad.beginShape(QUADS);
        quad.noStroke();
        quad.texture(tex);
        quad.textureMode(NORMAL);
        for (PShape pShape : r.getChildren()) {
            if (pShape.getVertexCount() == 3) {
                fillInShape(tri,pShape);
            }
            if (pShape.getVertexCount() == 4) {
                fillInShape(quad,pShape);
            }
        }
        tri.endShape();
        quad.endShape();
    }
    private void fillInShape(PShape shape, PShape child) {
        for (int j=0; j<child.getVertexCount(); j++) {
            PVector p = child.getVertex(j);
            PVector n = child.getNormal(j);
            float u = child.getTextureU(j);
            float v = child.getTextureV(j);
            shape.normal(n.x, n.y, n.z);
            shape.vertex(p.x, p.y, p.z, u, v);
        }
    }
    void draw() {
        __instance.shape(tri);
        __instance.shape(quad);
    }
}
