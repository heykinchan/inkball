package inkball;
import java.util.ArrayList;

import processing.core.*;

/**
 * Represents a point on the game board in the Inkball game.
 * <p>
 * The {@code Point} class is used to define a specific point in 2D space with floating-point precision.
 * It is commonly used for tracking positions of drawn paths or objects in the game.
 */
public class Point extends GameObject{
    
    private float xFloat;
    private float yFloat;

    /**
     * Constructs a {@code Point} with specified x and y coordinates.
     *
     * @param xf the x-coordinate of the point as a float
     * @param yf the y-coordinate of the point as a float
     */
    public Point(float xf, float yf){
        super((int)xf,(int)yf);
        this.xFloat = xf;
        this.yFloat = yf;
        this.type = "point";
    }

    /**
     * Gets the x-coordinate of the point in float precision.
     *
     * @return the x-coordinate as a float
     */
    public float getXF(){
        return xFloat;
    }

    /**
     * Gets the y-coordinate of the point in float precision.
     *
     * @return the y-coordinate as a float
     */
    public float getYF(){
        return yFloat;
    }

}
