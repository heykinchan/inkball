package inkball;

/**
 * Represents a cell on the game board in the Inkball game.
 * <p>
 * Each {@code Cell} object has a position and may contain a {@code GameObject},
 * such as a wall, hole, or spawner.
 */
public class Cell {
    
    // Pre-defined attributes upon creation
    private int x;
    private int y;

    // The item on the cell
    private GameObject object;

    /**
     * Constructs a cell at the specified coordinates with no initial object.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     */
    public Cell(int x, int y){
        this.x = x;
        this.y = y;
        this.object = null;
    }

    /**
     * Returns the type of object currently placed in the cell.
     *
     * @return a string representing the object type in the cell, such as "tile", "wall", "hole", "spawner", or "unknown"
     */
    public String getType(){
        if(this.object == null){
            return "tile";
        } else if (this.object instanceof Wall){
            return "wall";
        } else if (this.object instanceof Hole){
            return "hole";
        } else if (this.object instanceof Spawner){
            return "spawner";
        } else {
            return "unknown";
        }
    }
    
     /**
     * Retrieves the {@code GameObject} in this cell.
     *
     * @return the {@code GameObject} contained in the cell, or {@code null} if empty
     */
    public GameObject getGameObject(){
        return object;
    }

    /**
     * Sets a {@code GameObject} in this cell.
     *
     * @param obj the {@code GameObject} to be placed in the cell
     */
    public void setGameObject(GameObject obj){
        this.object = obj;
    }

    /**
     * Draws the cell in the game application window if the cell type is "tile".
     *
     * @param app the application window in which the cell is drawn
     */
    public void draw(App app){
        if(this.getType() == "tile"){
            app.image(app.getSprite("tile"),x,y);
        }
    }
}
