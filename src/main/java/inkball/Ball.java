package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;


/**
 * Represents a ball in the Inkball game.
 *
 * <p>The {@code Ball} class manages the state, movement, and interactions of a ball object within
 * the game, including handling velocity, collisions with boundaries and lines, and interactions with
 * other game elements like spawners and holes.
 */
public class Ball extends GameObject{

    private int color;
    public float velocityX;
    public float velocityY;
    private float xFloat;
    private float yFloat;
    private float lastXF;
    private float lastYF;
    private float nextXF;
    private float nextYF;
    private boolean absorbed;
    public float displayProp;
    public boolean beingAbsorbed;
    public boolean lineCollided;
    public boolean wallCollided;

    /**
     * Creates a new {@code Ball} at the specified coordinates with the given color.
     *
     * @param x the x-coordinate of the ball's starting position
     * @param y the y-coordinate of the ball's starting position
     * @param c the color identifier for the ball
     */
    public Ball(int x, int y, int c){
        super(x,y);
        this.color = c;
        this.type = "ball";
        this.xFloat = x;
        this.yFloat = y;
        this.lastXF = x;
        this.lastYF = y;
        this.nextXF = x + velocityX;
        this.nextYF = y + velocityY;
        this.absorbed = false;
        this.displayProp = 1;
        this.beingAbsorbed = false;
        this.lineCollided = false;
        this.wallCollided = false;

    }

    /**
     * Returns the current x-axis velocity of the ball.
     *
     * @return the ball's x-axis velocity
     */
    public float getVX(){
        return velocityX;
    }

     /**
     * Returns the current y-axis velocity of the ball.
     *
     * @return the ball's y-axis velocity
     */
    public float getVY(){
        return velocityY;
    }

    /**
     * Returns the floating-point x-coordinate of the ball.
     *
     * @return the x-coordinate of the ball
     */
    public float getXF(){
        return xFloat;
    }

    /**
     * Returns the floating-point y-coordinate of the ball.
     *
     * @return the y-coordinate of the ball
     */
    public float getYF(){
        return yFloat;
    }

    /**
     * Returns the next x-coordinate of the ball based on its velocity.
     *
     * @return the next x-coordinate of the ball
     */
    public float nextX(){
        return nextXF;
    }

     /**
     * Returns the next y-coordinate of the ball based on its velocity.
     *
     * @return the next y-coordinate of the ball
     */
    public float nextY(){
        return nextYF;
    }

    /**
     * Returns the previous x-coordinate of the ball.
     *
     * @return the previous x-coordinate of the ball
     */
    public float lastX(){
        return lastXF;
    }
    /**
     * Returns the previous y-coordinate of the ball.
     *
     * @return the previous y-coordinate of the ball
     */
    public float lastY(){
        return lastYF;
    }

    /**
     * Returns the color of the ball.
     *
     * @return the color identifier of the ball
     */
    public int getColor(){
        return color;
    }

    /**
     * Sets the color of the ball.
     *
     * @param color the color identifier to set
     */
    public void setColor(int color){
        this.color = color;
    }

    /**
     * Spawns the ball at the location of the specified spawner and sets a random velocity.
     *
     * @param s the spawner from which to spawn the ball
     */
    public void spawn(Spawner s){
        this.xFloat = s.getX();
        this.yFloat = s.getY();
        this.lastXF = x;
        this.lastYF = y;
        randomVelocity();
        updateNextPos();
    }

    /**
     * Draws the ball on the specified application window.
     *
     * @param app the {@code App} instance on which to draw the ball
     */
    public void draw(App app){
        checkBoundaryCollision();
        if((x >= 0 && x + App.BALLSIZE < App.WIDTH && y >= App.TOPBAR && y + App.BALLSIZE <= App.HEIGHT) && (!absorbed)){
            PImage ballRawImage = app.getSprite("ball" + String.valueOf(color));
            app.image(ballRawImage,xFloat,yFloat,ballRawImage.width * displayProp, ballRawImage.height * displayProp);
        }
    }

    /**
     * Draws the ball in an unspawned state at the specified coordinates.
     *
     * @param app the {@code App} instance on which to draw the ball
     * @param xF  the x-coordinate for drawing the unspawned ball
     * @param yF  the y-coordinate for drawing the unspawned ball
     */
    public void drawUnspawned(App app, float xF, float yF){
        app.image(app.getSprite("ball" + String.valueOf(color)),xF,yF);
    }
    
    /**
     * Updates the ball's position based on its velocity.
     */
    public void updatePos(){
        if(!absorbed){
            lastXF = xFloat;
            lastYF = yFloat;
            xFloat += velocityX;
            yFloat += velocityY;
            x = (int) xFloat;
            y = (int) yFloat;
            updateNextPos();
        }
    }

    /**
     * Updates the ball's next position based on its current velocity.
     */
    public void updateNextPos(){
        nextXF = xFloat + velocityX;
        nextYF = yFloat + velocityY;
    }

    /**
     * Checks if the ball has collided with the boundaries of the game area and adjusts its velocity.
     */
    public void checkBoundaryCollision(){
        if((xFloat < 0 || xFloat + App.BALLSIZE > App.WIDTH) && (yFloat < App.TOPBAR || yFloat + App.BALLSIZE > App.HEIGHT)){
            velocityX *= -1;
            velocityY *= -1;
        } else {
            if(xFloat < 0 || xFloat + App.BALLSIZE > App.WIDTH){
                velocityX *= -1;
            }
            if(yFloat < App.TOPBAR || yFloat + App.BALLSIZE > App.HEIGHT){
                velocityY *= -1;
            }
        }
        updateNextPos();
    }

    /**
     * Sets a random initial velocity for the ball.
     */
    public void randomVelocity(){
        Random random = new Random();
        // Randomise the selection of X velocity
        if(random.nextBoolean()){
            this.velocityX = 2;
        } else {
            this.velocityX = -2;
        }
        // Same for Y velocity
        if(random.nextBoolean()){
            this.velocityY = 2;
        } else {
            this.velocityY = -2;
        }
        updateNextPos();
    }

    /**
     * Marks the ball as absorbed.
     */
    public void absorb(){
        this.absorbed = true;
    }

    /**
     * Checks if the ball is absorbed.
     *
     * @return {@code true} if the ball is absorbed, otherwise {@code false}
     */
    public boolean IsAbsorbed(){
        return this.absorbed;
    }

    /**
     * Resets the ball's coordinates, effectively placing it out of the game area.
     */
    public void backToQueue(){
        this.x = -10;
        this.y = -10;
        this.xFloat = -10;
        this.yFloat = -10;
    }

    /**
     * Updates the ball's velocity with specified x and y values.
     *
     * @param vx the new x-axis velocity
     * @param vy the new y-axis velocity
     */
    public void updateVelocity(float vx, float vy){
        this.velocityX = vx;
        this.velocityY = vy;
        updateNextPos();
    }

   /**
     * Checks if the ball collides with a given line.
     *
     * @param line the list of points representing the line
     * @return {@code true} if a collision occurs, otherwise {@code false}
     */
    public boolean checkLineCollision(ArrayList<Point> line){
        // Check each part of the line to see if there is any collision
        for(int i = 0; i < line.size()-1; i++){
            float p1x = line.get(i).getXF();
            float p1y = line.get(i).getYF();
            float p2x = line.get(i + 1).getXF();
            float p2y = line.get(i + 1).getYF();
            float ballCentreX = xFloat + App.BALLSIZE / 2;
            float ballCentreY = yFloat + App.BALLSIZE / 2;
            
            // Check if there is a collision
            double distanceP1 = Math.sqrt(Math.pow((p1x - ballCentreX), 2) + Math.pow((p1y - ballCentreY), 2));
            double distanceP2 = Math.sqrt(Math.pow((p2x - ballCentreX), 2) + Math.pow((p2y - ballCentreY), 2));
            double lineLength = Math.sqrt(Math.pow((p2x - p1x), 2) + Math.pow((p2y - p1y), 2)) + App.BALLSIZE / 2;
            if(distanceP1 + distanceP2 < lineLength + App.BALLSIZE){
                lineCollided = true;
                // Calculate vector N1 and N2
                float dx = p2x - p1x;
                float dy = p2y - p1y;
                float nLength = (float) Math.sqrt(dx * dx + dy * dy);
                // Calculate the normalised n1 and n2
                float n1x = -dy / nLength;
                float n1y = dx / nLength;
                float n2x = dy / nLength;
                float n2y = -dx / nLength;
                // Calculate midpoints for checking which normal to use
                float midx = (p1x + p2x) / 2f;
                float midy = (p1y + p2y) / 2f;
                // Check which normal vector is closer to the ball's position
                double distanceN1 = Math.sqrt(Math.pow((midx + n1x - ballCentreX), 2) + Math.pow((midy + n1y - ballCentreY), 2));
                double distanceN2 = Math.sqrt(Math.pow((midx + n2x - ballCentreX), 2) + Math.pow((midy + n2y - ballCentreY), 2));
                
                if(distanceN1 < distanceN2){
                    float dotProduct = velocityX * n1x + velocityY * n1y;
                    float ux = velocityX - 2 * dotProduct * n1x;
                    float uy = velocityY - 2 * dotProduct * n1y;
                    updateVelocity(ux, uy);
                } else if (distanceN1 > distanceN2) {
                    float dotProduct = velocityX * n2x + velocityY * n2y;
                    float ux = velocityX - 2 * dotProduct * n2x;
                    float uy = velocityY - 2 * dotProduct * n2y;
                    updateVelocity(ux, uy);
                }

                return true;
            }
        }
        return false;
    }
    
}
