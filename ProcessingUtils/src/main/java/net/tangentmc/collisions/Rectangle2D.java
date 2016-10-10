package net.tangentmc.collisions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import processing.core.PVector;

/**
 * Rectangle collisions
 * Allows you to create a rectangle, and check if a point is inside it or if it collides.
 */
@AllArgsConstructor
@Getter
@Setter
public class Rectangle2D {
    private float x,y,width,height;
    public Rectangle2D(Rectangle2D other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
    }
    public boolean intersects(Rectangle2D other) {
        return x+width > other.getX() && x < other.getX()+other.getWidth() &&
                y+height > other.getY() && y < other.getY()+other.getHeight();
    }
    public boolean contains(PVector point) {
        return contains(point.x,point.y);
    }
    public boolean contains(float x, float y) {
        return x > this.x && x < this.x+this.width && y > this.y && y < this.y+this.height;
    }
}
