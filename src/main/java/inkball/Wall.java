package inkball;

/**
 * Represents a wall or brick object in the Inkball game. This wall can either be a solid wall or a breakable brick,
 * with the ability to detect and respond to collisions with a ball, count hits, and rotate position if applicable.
 * <p>
 * Walls are represented with a specific colour and can affect the ball's velocity upon collision.
 * Bricks break after a certain number of hits.
 */
public class Wall extends GameObject {

    private int color;
    private boolean isBrick;
    private int numHits;

    /**
     * Constructs a wall or brick object at the specified coordinates with a defined colour.
     *
     * @param x       the x-coordinate of the wall
     * @param y       the y-coordinate of the wall
     * @param c       the colour identifier of the wall
     * @param isBrick {@code true} if this wall is a breakable brick; {@code false} if it is a solid wall
     */
    public Wall(int x, int y,int c, boolean isBrick){
        super(x,y);
        this.color = c;
        this.type = "wall";
        this.isBrick = isBrick;
        this.numHits = 0;
    }

    /**
     * Draws the wall on the provided game application window. Bricks and walls are drawn differently based on their type.
     *
     * @param app the application window in which the wall is drawn
     */
    public void draw(App app){
        if(isBrick){
            app.image(app.getSprite("brick" + String.valueOf(color)),x,y);    
        } else{
            app.image(app.getSprite("wall" + String.valueOf(color)),x,y);
        }
        
    }

    /**
     * Gets the colour identifier of the wall.
     *
     * @return the colour identifier of this wall
     */
    public int getColor(){
        return color;
    }

    /**
     * Checks if this brick has broken due to reaching the maximum hit limit.
     *
     * @return {@code true} if the brick is broken; {@code false} otherwise
     */
    public boolean checkBrickBroken(){
        if(this.isBrick && numHits >= 3){
            return true;
        }
        return false;
    }

    /**
     * Rotates the wall's position in a circular motion within the game boundaries.
     * <p>
     * The wall moves to adjacent cells in a clockwise manner.
     */
    public void rotate(){
        int xIndex = x / App.CELLSIZE;
        int yIndex = (y - App.TOPBAR) / App.CELLSIZE;
        if(yIndex == 0 && xIndex >= 0 && xIndex < App.BOARD_WIDTH - 1 ){
            x += App.CELLSIZE;
        }
        else if (xIndex == App.BOARD_WIDTH - 1 && yIndex < App.BOARD_HEIGHT - 1 ){
            y += App.CELLSIZE;
        }
        else if (yIndex == App.BOARD_HEIGHT -1 && xIndex > 0){
            x -= App.CELLSIZE;
        } else {
            y -= App.CELLSIZE;
        }
    }

    /**
     * Checks and responds to a collision between this wall and the specified ball. Updates the ball's velocity
     * and wall colour if necessary, and records the number of hits on this wall.
     * <p>
     * Handles edge and corner cases to prevent the ball from getting trapped at the wall boundaries.
     *
     * @param ball the ball to check for collision with this wall
     * @param app  the application window for accessing the game board state
     */
    public void checkCollision(Ball ball, App app){
        // Calculate the row index and column index of the cell
        int X = x/App.WALLSIZE;
        int Y = (y-App.TOPBAR) / App.WALLSIZE;

        // If the collision is at the top of the wall
        if(x < ball.getXF() + App.BALLSIZE && x + App.WALLSIZE > ball.getXF() && y < ball.getYF() + App.BALLSIZE && y > ball.getYF()){

            // Update that the ball has been collided with a wall
            ball.wallCollided = true;
            // Update the color of the ball if needed
            if(color > 0 && ball.getColor() != color){
                ball.setColor(color);
            }
            // Record the number of hits
            if(ball.getColor() == color || color == 0){
                numHits++;
            }
            // Check if it hits the top left corner
            if(x > ball.getXF() && x > 0){
                // Check if the nearby cells have wall
                // If it sticks to the top boundary of the game board
                if (Y == 0){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same column, but no diagonal wall
                else if(app.board[Y-1][X].getType().equals("wall") && (!app.board[Y-1][X-1].getType().equals("wall"))){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same row, but no diagonal wall
                else if (app.board[Y][X-1].getType().equals("wall") && (!app.board[Y-1][X-1].getType().equals("wall"))){
                    ball.velocityY *= -1;
                }
                // If no walls nearby
                else if(!app.board[Y][X-1].getType().equals("wall") && (!app.board[Y-1][X].getType().equals("wall"))){
                    // If it comes from the top side
                    if (ball.lastX() + App.BALLSIZE >= x){
                        ball.velocityY *= -1;
                    } 
                    // if it comes from the left side
                    else if (ball.lastY() + App.BALLSIZE >= y){
                        ball.velocityX *= -1;
                    }
                    // if it hits diagonally
                    else {
                        ball.velocityY *= -1;
                        ball.velocityX *= -1;
                    }
                }
                // if there is diagonal wall
                else {
                    ball.velocityY *= -1;
                    ball.velocityX *= -1;
                }

                //Mark the hit for the walls nearby
                if(Y>0 && app.board[Y-1][X].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y-1][X].getGameObject();
                    if(ball.getColor() == nextWall.getColor() || nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(Y>0 && app.board[Y-1][X-1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y-1][X-1].getGameObject();
                    if(ball.getColor() == nextWall.getColor() || nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(app.board[Y][X-1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y][X-1].getGameObject();
                    if(ball.getColor() == nextWall.getColor() || nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
            }
            // Check if it hits the top right corner
            else if(x + App.WALLSIZE < ball.getXF() + App.BALLSIZE && x + App.WALLSIZE < App.WIDTH){
                // Check if the nearby cells have wall
                // If it sticks to the top boundary of the game board
                if (Y == 0){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same column, but no diagonal wall
                else if(app.board[Y-1][X].getType().equals("wall") && (!app.board[Y-1][X+1].getType().equals("wall"))){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same row, but no diagonal wall
                else if (app.board[Y][X+1].getType().equals("wall") && (!app.board[Y-1][X+1].getType().equals("wall"))){
                    ball.velocityY *= -1;
                }
                // If no walls nearby
                else if(!app.board[Y][X+1].getType().equals("wall") && (!app.board[Y-1][X].getType().equals("wall"))){
                    // If it comes from the top side
                    if (ball.lastX() <= x + App.WALLSIZE){
                        ball.velocityY *= -1;
                    } 
                    // if it comes from the right side
                    else if (ball.lastY() + App.BALLSIZE >= y){
                        ball.velocityX *= -1;
                    }
                    // if it hits diagonally
                    else {
                        ball.velocityY *= -1;
                        ball.velocityX *= -1;
                    }
                }
                // if there is diagonal wall
                else {
                    ball.velocityY *= -1;
                    ball.velocityX *= -1;
                }
                //Mark the hit for the walls nearby
                if(Y>0 && app.board[Y-1][X].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y-1][X].getGameObject();
                    if(ball.getColor() == nextWall.getColor() || nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(Y>0 && app.board[Y-1][X+1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y-1][X+1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()|| nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(app.board[Y][X+1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y][X+1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()|| nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
            }
            // Hit in the middle of the top side
            else {
                ball.velocityY *= -1;
                // Prevent trapping in the top border
                if(y < ball.lastY() + App.BALLSIZE && y > ball.lastY()){
                    ball.velocityY = -Math.abs(ball.velocityY);
                } 
            }
        } 

        // If the collision is at the bottom of the wall
        else if(x < ball.getXF() + App.BALLSIZE && x + App.WALLSIZE > ball.getXF() && y + App.WALLSIZE > ball.getYF() && y + App.WALLSIZE < ball.getYF() + App.BALLSIZE){


            // Update that the ball has been collided with a wall
            ball.wallCollided = true;
            // Update the color of the ball if needed
            if(color > 0 && ball.getColor() != color){
                ball.setColor(color);
            }
            // Record the number of hits
            if(ball.getColor() == color || color == 0){
                numHits++;
            }
            // Check if it hits the bottom left corner
            if(x > ball.getXF() && x > 0){
                // Check if the nearby cells have wall
                // If it sticks to the bottom boundary of the game board
                if (Y == 17){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same column, but no diagonal wall
                else if(app.board[Y+1][X].getType().equals("wall") && (!app.board[Y+1][X-1].getType().equals("wall"))){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same row, but no diagonal wall
                else if (app.board[Y][X-1].getType().equals("wall") && (!app.board[Y+1][X-1].getType().equals("wall"))){
                    ball.velocityY *= -1;
                }
                // If no walls nearby
                else if(!app.board[Y+1][X].getType().equals("wall") && (!app.board[Y][X-1].getType().equals("wall"))){
                    // If it comes from the bottom side
                    if (ball.lastX() + App.BALLSIZE >= x){
                        ball.velocityY *= -1;
                    } 
                    // if it comes from the left side
                    else if (ball.lastY() <= y + App.WALLSIZE){
                        ball.velocityX *= -1;
                    }
                    // if it hits diagonally
                    else {
                        ball.velocityY *= -1;
                        ball.velocityX *= -1;
                    }
                }
                // if there is diagonal wall
                else {
                    ball.velocityY *= -1;
                    ball.velocityX *= -1;
                }
                //Mark the hit for the walls nearby
                if(Y<17 && app.board[Y+1][X].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y+1][X].getGameObject();
                    if(ball.getColor() == nextWall.getColor() || nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(Y<17 && app.board[Y+1][X-1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y+1][X-1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()|| nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
                if(app.board[Y][X-1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y][X-1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()|| nextWall.getColor() == 0){
                        nextWall.numHits++;
                    }
                }
            }
            // Check if it hits the bottom right corner
            else if(x + App.WALLSIZE < ball.getXF() + App.BALLSIZE && x + App.WALLSIZE < App.WIDTH){
                // Check if the nearby cells have wall
                // If it sticks to the bottom boundary of the game board
                if (Y == 17){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same column, but no diagonal wall
                else if(app.board[Y+1][X].getType().equals("wall") && (!app.board[Y+1][X+1].getType().equals("wall"))){
                    ball.velocityX *= -1;
                }
                // If there is a linked wall on the same row, but no diagonal wall
                else if (app.board[Y][X+1].getType().equals("wall") && (!app.board[Y+1][X+1].getType().equals("wall"))){
                    ball.velocityY *= -1;
                }
                // If no walls nearby
                else if(!app.board[Y+1][X].getType().equals("wall") && (!app.board[Y][X+1].getType().equals("wall"))){
                    // If it comes from the bottom side
                    if (ball.lastX() <= x + App.WALLSIZE){
                        ball.velocityY *= -1;
                    } 
                    // if it comes from the right side
                    else if (ball.lastY() <= y + App.WALLSIZE){
                        ball.velocityX *= -1;
                    }
                    // if it hits diagonally
                    else {
                        ball.velocityY *= -1;
                        ball.velocityX *= -1;
                    }
                }
                // if there is diagonal wall
                else {
                    ball.velocityY *= -1;
                    ball.velocityX *= -1;
                }
                //Mark the hit for the walls nearby
                if(Y<17 && app.board[Y+1][X].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y+1][X].getGameObject();
                    if(ball.getColor() == nextWall.getColor()){
                        nextWall.numHits++;
                    }
                }
                if(Y<17 && app.board[Y+1][X+1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y+1][X+1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()){
                        nextWall.numHits++;
                    }
                }
                if(app.board[Y][X+1].getType().equals("wall")){
                    Wall nextWall = (Wall) app.board[Y][X+1].getGameObject();
                    if(ball.getColor() == nextWall.getColor()){
                        nextWall.numHits++;
                    }
                }
            }
            // Hit in the middle of the top side
            else {
                ball.velocityY *= -1;
                // Prevent the ball trapped in the bottom border
                if(y + App.WALLSIZE > ball.lastY() && y + App.WALLSIZE < ball.lastY() + App.BALLSIZE){
                    ball.velocityY = Math.abs(ball.velocityY);    
                }                
            }
        } 
        
        // If the collision is at the left of the wall
        else if(x < ball.getXF() + App.BALLSIZE && x > ball.getXF() && y < ball.getYF() + App.BALLSIZE && y + App.WALLSIZE > ball.getYF()){

            // Update that the ball has been collided with a wall
            ball.wallCollided = true;
            // Update the color of the ball if needed
            if(color > 0 && ball.getColor() != color){
                ball.setColor(color);
            }
            // Record the number of hits
            if(ball.getColor() == color || color == 0){
                numHits++;
            }
            ball.velocityX *= -1;
            //Prevent the ball trapped in the left border
            if(x < ball.lastX() + App.BALLSIZE && x > ball.lastX()){
                ball.velocityX = -Math.abs(ball.velocityX);
            }
            
        } 
        
        // if the collision is at the right of the wall
        else if (x + App.WALLSIZE > ball.getXF() && x + App.WALLSIZE < ball.getXF() + App.BALLSIZE && y < ball.getYF() + App.BALLSIZE && y + App.WALLSIZE > ball.getYF()){

            // Update that the ball has been collided with a wall
            ball.wallCollided = true;
            // Update the color of the ball if needed
            if(color > 0 && ball.getColor() != color){
                ball.setColor(color);
            }
            // Record the number of hits
            if(ball.getColor() == color || color == 0){
                numHits++;
            }
            ball.velocityX *= -1;
            // Prevent the ball trapped in the right border
            if(x + App.WALLSIZE > ball.lastX() && x + App.WALLSIZE < ball.lastX() + App.BALLSIZE){
                ball.velocityX = Math.abs(ball.velocityX);
            }
        }
        ball.updateNextPos();
    }
}
