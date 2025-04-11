package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import processing.core.PApplet;
import processing.event.KeyEvent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Main application class for the Inkball game.
 *
 * <p>The {@code App} class is responsible for initializing game resources, handling player input,
 * and managing game states such as start, pause, and game-over conditions. This class integrates
 * different components of the game, enabling coordinated gameplay flow.
 */
public class SampleTest {

    static App app;

    /**
     * Sets up the game environment for all tests.
     *
     * <p>This method initialises the {@code App} instance, starts the application loop, and
     * prepares the game environment. It is called once before any tests are executed.
     */
    @BeforeAll
    public static void setup() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup();
    }

    /**
     * Tests the game's initialization and start conditions.
     *
     * <p>This test validates that the game begins in the correct state and that initial values and
     * components are set up properly. This test is particularly useful for verifying game launch
     * sequences and ensuring that no required components are null or uninitialized.
     *
     * @throws NullPointerException if any essential game components are missing
     */
    @Test
    public void testSetUp() {
        app.setup();
        assertTrue(
            app.lastSpawnTime == app.starttime
            && app.gameOver == false
            && app.gameWin == false
            && app.levelScore == 0
            && app.paused == false
        );
    }

    /**
     * Tests the game's main loop for game state progression.
     *
     * <p>This test verifies that the game runs continuously without triggering the game-over
     * condition prematurely, validating that the main loop functions within expected limits.
     */
    @Test
    public void testRunning() {
        app.reset();
        int runtime = 0;
        while((!app.gameOver) && runtime < 1000){
            app.draw();
            runtime++;
        }
        
        assertTrue(
            (!app.gameOver) && runtime <= 1000
        );
    }

    /**
     * Tests the pause functionality to ensure the game pauses correctly when the space key is pressed.
     *
     * <p>This test simulates pressing the space bar and checks that the game correctly transitions
     * into a paused state.
     */
    @Test
    public void testPause() {
        app.reset();

        app.key = ' ';  
        app.keyCode = ' ';  

        // Call keyReleased directly
        app.keyReleased();

        assertTrue(
            app.paused
        );
    }

    /**
     * Tests the drawing of a line on the game board.
     *
     * <p>This test simulates a mouse press, drag, and release to create a line, and verifies that
     * a new line has been added to the {@code lines} collection in the game.
     */
    @Test
    public void testDrawLine() {
        app.reset();
        app.mouseX = 4 * app.CELLSIZE;
        app.mouseY = 7 * app.CELLSIZE + app.TOPBAR;
        app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
        app.mousePressed(null);  // Call mousePressed

        // Simulate pressing the mouse at position (100, 100)
        for(int y = 7 * app.CELLSIZE + app.TOPBAR; y < 10 * app.CELLSIZE + app.TOPBAR; y ++ ){
            app.mouseX = 4 * app.CELLSIZE;
            app.mouseY = y;
            app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
            app.mouseDragged(null);  // Call mouseDragged
        }
        // Simulate releasing the mouse button
        app.mouseReleased(null);  // Call mouseReleased

        assertFalse(
            app.lines.isEmpty()
        );
    }

    /**
     * Tests the removal of a line from the game board.
     *
     * <p>This test simulates drawing and then erasing a line by pressing the left mouse button
     * to draw and the right button to erase. It verifies that the line collection becomes empty.
     */
    @Test
    public void testRemoveLine() {
        app.reset();
        app.mouseX = 4 * app.CELLSIZE;
        app.mouseY = 7 * app.CELLSIZE + app.TOPBAR;
        app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
        app.mousePressed(null);  // Call mousePressed

        // Simulate pressing the mouse at position (100, 100)
        for(int y = 7 * app.CELLSIZE + app.TOPBAR; y < 10 * app.CELLSIZE + app.TOPBAR; y ++ ){
            app.mouseX = 4 * app.CELLSIZE;
            app.mouseY = y;
            app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
            app.mouseDragged(null);  // Call mouseDragged
        }
        // Simulate releasing the mouse button
        app.mouseReleased(null);  // Call mouseReleased

        // Simulate pressing the mouse at position (100, 100)
        for(int y = 7 * app.CELLSIZE + app.TOPBAR; y < 10 * app.CELLSIZE + app.TOPBAR; y ++ ){
            app.mouseX = 4 * app.CELLSIZE;
            app.mouseY = y;
            app.mouseButton = PApplet.RIGHT;  // Simulate left mouse button
            app.mouseDragged(null);  // Call mouseDragged
        }

        assertTrue(
            app.lines.isEmpty()
        );
    }

    /**
     * Tests collision detection between balls and player-drawn lines.
     *
     * <p>This test verifies that a ball collides with a line under specific conditions, impacting
     * game progression and player interactions with game objects.
     *
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @Test
    public void testCollideWLine() throws InterruptedException {
        app.setup();
        app.mouseX = 5 * app.CELLSIZE;
        app.mouseY = 7 * app.CELLSIZE + app.TOPBAR;
        app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
        app.mousePressed(null);  // Call mousePressed

        // Simulate pressing the mouse at position (100, 100)
        for(int y = 7 * app.CELLSIZE + app.TOPBAR; y < 10 * app.CELLSIZE + app.TOPBAR; y ++ ){
            app.mouseX = 5 * app.CELLSIZE;
            app.mouseY = y;
            app.mouseButton = PApplet.LEFT;  // Simulate left mouse button
            app.mouseDragged(null);  // Call mouseDragged
        }

        int totalFrames = 1000;

        for (int i = 0; i < totalFrames; i++) {
            app.draw();  // Simulate a frame
            Thread.sleep(5);
        }
        
        assertTrue(app.ballsOnBoard.size() != 0 || app.gameOver);
    }

     /**
     * Tests the level-up functionality and verifies that time remains after leveling up.
     *
     * <p>This test ensures that upon leveling up, the remaining time is preserved and
     * updated correctly.
     *
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @Test
    public void testLevelUp() throws InterruptedException {
        app.setup();
        for (int i = 0; i < 500; i++){
            app.levelUp = true;
            app.remainingTime = 10000;
            app.draw();
            Thread.sleep(10);
        }
        assertTrue(app.remainingTime > 0);
    }

}

// gradle run						Run the program
// gradle test						Run the testcases

// Please ensure you leave comments in your testcases explaining what the testcase is testing.
// Your mark will be based off the average of branches and instructions code coverage.
// To run the testcases and generate the jacoco code coverage report: 
// gradle test jacocoTestReport
