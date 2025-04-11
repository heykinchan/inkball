package inkball;

import java.io.*;
import java.util.*;

import com.google.common.collect.Queues;

/**
 * Represents a hole on the game board in the Inkball game, capable of absorbing balls.
 * <p>
 * The {@code Hole} class inherits from {@code GameObject} and interacts with {@code Ball}
 * objects by attempting to absorb them based on their proximity and colour match.
 */
public class Hole extends GameObject{

    private int color;

    /**
     * Constructs a hole at the specified coordinates with a specified colour.
     *
     * @param x the x-coordinate of the hole
     * @param y the y-coordinate of the hole
     * @param c the colour identifier for the hole
     */
    public Hole(int x, int y,int c){
        super(x,y);
        this.color = c;
        this.type = "hole";
    }

    /**
     * Draws the hole on the game application window.
     *
     * @param app the application window in which the hole is drawn
     */
    public void draw(App app){
        app.image(app.getSprite("hole" + String.valueOf(color)),x,y);
    }

    /**
     * Checks if a ball is within absorbing range of the hole, and absorbs it if certain
     * conditions are met (e.g., matching colours). Updates the game score based on the
     * success or failure of the absorption.
     *
     * @param ball the ball object to check for absorption
     * @param app  the application window managing the game state
     */
    public void checkAbsorb(Ball ball, App app){
        float ballCentreX = ball.getXF() + App.BALLSIZE/2;
        float ballCentreY = ball.getYF() + App.BALLSIZE/2;
        float holeCentreX = x + App.CELLSIZE;
        float holeCentreY = y + App.CELLSIZE;
        // Check if the ball can be absorbed
        if((int)ballCentreX < (int)holeCentreX + 10 && (int)ballCentreX > (int)holeCentreX - 10 && (int)ballCentreY < (int)holeCentreY + 10 && (int)ballCentreY > (int)holeCentreY - 10){
            app.ballsOnBoard.remove(ball);
            // Check if the color is matched
            if(color == 0 || ball.getColor() == 0 || color == ball.getColor()){
                ball.absorb();
                // Add the score
                app.levelScore += app.score_increase_from_hole_capture_modifier[app.currentLevel] * app.score_increase_from_hole_capture.get(ball.getColor());
            } else {
                app.ballQueue.add(ball);
                ball.backToQueue();
                app.levelScore -= app.score_decrease_from_wrong_hole_modifier[app.currentLevel] * app.score_decrease_from_wrong_hole.get(ball.getColor());
                if(app.ballQueue.size()==1){
                    app.lastSpawnTime = app.millis();
                }
            }
        }
        // If the ball cannot be absorbed but near the hole to adjust the velocity
        else if(Math.sqrt((ballCentreX- holeCentreX)* (ballCentreX- holeCentreX) + (ballCentreY - holeCentreY)*(ballCentreY - holeCentreY)) <= (double)App.CELLSIZE){
            float ax = (holeCentreX - ballCentreX) * 0.005f;
            float ay = (holeCentreY - ballCentreY) * 0.005f;
            ball.updateVelocity(ball.getVX() + ax, ball.getVY() + ay);
            ball.displayProp = (float)(Math.sqrt((ballCentreX- holeCentreX)* (ballCentreX- holeCentreX) + (ballCentreY - holeCentreY)*(ballCentreY - holeCentreY))/(double)App.CELLSIZE);
            ball.beingAbsorbed = true;
        } else {
            if(!ball.beingAbsorbed){
                ball.displayProp = 1;
            }
        }
        
    }
}
