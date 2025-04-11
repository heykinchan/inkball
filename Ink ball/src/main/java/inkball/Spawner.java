package inkball;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a spawner object in the Inkball game that is responsible for spawning balls
 * onto the game board at specified intervals.
 * <p>
 * The {@code Spawner} class inherits from {@code GameObject} and interacts with the
 * {@code Ball} objects by transferring balls from the ball queue to the active game area.
 */
public class Spawner extends GameObject{

    /**
     * Constructs a spawner at the specified coordinates.
     *
     * @param x the x-coordinate of the spawner
     * @param y the y-coordinate of the spawner
     */
    public Spawner(int x, int y){
        super(x,y);
        this.type = "spawner";
    }

    /**
     * Draws the spawner on the game application window.
     *
     * @param app the application window in which the spawner is drawn
     */
    public void draw(App app){
        app.image(app.getSprite("entrypoint"),x,y);
    }

    /**
     * Spawns a ball from the {@code ballQueue} onto the game board at the spawner's position
     * and adds it to the list of balls currently on the board.
     *
     * @param ballQueue    the queue of balls waiting to be spawned
     * @param ballsOnBoard the list of balls currently active on the game board
     */
    public void spawn(Queue<Ball> ballQueue, ArrayList<Ball> ballsOnBoard){
        if(!ballQueue.isEmpty()){
            Ball nextBall = ballQueue.poll();
            ballsOnBoard.add(nextBall);
            nextBall.spawn(this);
        }
    }
}
