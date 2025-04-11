package inkball;

/**
 * Represents a generic game object in the Inkball game.
 * <p>
 * The {@code GameObject} class serves as an abstract base class for all objects in the game,
 * providing common properties and methods for setting and retrieving position.
 * Each subclass of {@code GameObject} represents a specific type of object in the game.
 */
public abstract class GameObject {
    
    // Pre-defined attributes upon creation
    protected int x;
    protected int y;
    protected String type = null;

    /**
     * Constructs a {@code GameObject} with specified x and y coordinates.
     *
     * @param x the x-coordinate of the game object
     * @param y the y-coordinate of the game object
     */
    public GameObject(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the game object.
     *
     * @return the x-coordinate of this object
     */
    public int getX(){
        return x;
    }

    /**
     * Gets the y-coordinate of the game object.
     *
     * @return the y-coordinate of this object
     */
    public int getY(){
        return y;
    }

}
