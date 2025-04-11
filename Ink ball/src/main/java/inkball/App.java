package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

/**
 * Main application class for the Inkball game.
 *
 * <p>The {@code App} class is responsible for initializing game resources, handling player input,
 * and managing game states such as start, pause, and game-over conditions. This class integrates
 * different components of the game, enabling coordinated gameplay flow.
 */

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 2 * CELLSIZE;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = (HEIGHT - TOPBAR)/CELLSIZE;
    public static final int BALLSIZE = 24;
    public static final int HOLESIZE = 64;
    public static final int WALLSIZE = 32;
    public static final int SPAWNERSIZE = 32;
    public static final int POINTSIZE = 10;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    // Additional attributes:
    private HashMap<String, PImage> sprites = new HashMap<>(); // For storing the pre-loaded images
    public Cell [][] board = new Cell[BOARD_HEIGHT][BOARD_WIDTH]; // For the set-up of the walls 
    // Attributes read from the config file
    public int totalLevel; // For storing the total number of levels
    public int currentLevel; // For tracking the current level
    public String[][] layouts; // For storing the layouts of each level
    public String[] layoutDir; // For storing the file directory of the layout of each level
    public int[] time; // For storing the maximum time allowed for playing for each level
    public int[] spawn_interval; // For storing the spawn internal of for each level
    public float[] score_increase_from_hole_capture_modifier; // For storing the increase modifier of for each level
    public float[] score_decrease_from_wrong_hole_modifier; // For storing the decrease modifer of for each level
    public ArrayList<ArrayList<String>> ballsRaw; // For storing the balls of each level
    public HashMap<Integer,Integer> score_increase_from_hole_capture = null; // For storing the score increase for correct hole capture
    public HashMap<Integer,Integer> score_decrease_from_wrong_hole = null; // For storing the score decrease for incorrect hole capture

    // Attributes for the gameplay
    public ArrayList<Ball> balls = null;
    public Queue<Ball> ballQueue = null;
    public ArrayList<Ball> ballsOnBoard = null;
    public ArrayList<Hole> holes = null;
    public ArrayList<Wall> walls = null;
    public ArrayList<Spawner> spawners = null;
    public ArrayList<ArrayList<Point>> lines = null;
    public int starttime = 0;
    public boolean gameOver;
    public boolean gameWin;
    public int totalScore;
    public int levelScore;
    public int lastSpawnTime;
    public boolean paused;
    public int pausedTime = 0;
    public boolean levelUp;
    public int remainingTime;
    public ArrayList<Wall> rotateBlock = null;
    public int numFrameForConversion;
    public int remainFrameForConversion;

    // Additional functions:
    /**
     * Accesses and returns the image associated with the given name.
     *
     * <p>If the image is not already loaded, this method attempts to load it and then stores it in
     * the {@code sprites} map for future access.
     *
     * @param s the name of the image to retrieve
     * @return the {@code PImage} object associated with the given name
     * @throws RuntimeException if the image cannot be found or decoded
     */
    public PImage getSprite(String s) {
        PImage result = sprites.get(s);
        if (result == null) {
            try{
            result = loadImage(URLDecoder.decode(this.getClass().getResource(s+".png").getPath(), StandardCharsets.UTF_8.name()));
            sprites.put(s, result);
            } catch (UnsupportedEncodingException e){
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * Loads the specified sprite images based on the provided name and count.
     *
     * <p>This method pre-loads one or multiple images with identifiers appended to the base name
     * when there are multiple instances to be loaded.
     *
     * @param obj the base name of the image(s) to load
     * @param n   the number of images to load (1 for a single image, or greater for multiple images)
     */
    public void loadSprite(String obj, int n){
        if(n == 1){
            getSprite(obj);
        } else if (n > 1) {
            for (int i = 0; i < n; i++) {
                getSprite(obj + String.valueOf(i));
            }
        }
    }

    /**
     * Pauses or resumes the game.
     *
     * <p>If the game is currently running, this method pauses the game, storing the time at which
     * the game was paused. If the game is already paused, it resumes gameplay, updating the start
     * time to include the paused duration.
     */
    public void pause(){
        // If it was paused before
        if ((time[currentLevel] > (millis() - starttime)/1000) && (!gameWin) ){
            if(paused){
                starttime += millis() - pausedTime;
                lastSpawnTime += millis() - pausedTime;
                paused = false;
            }
            // If it was running before
            else {
                paused = true;
                pausedTime = millis();
            }
        }
    }

    /**
     * Checks whether all balls have been absorbed by the correct holes.
     *
     * <p>If all balls are absorbed, the game progresses to the next level, or ends if the current
     * level is the last one. This method also updates the score and remaining time if applicable.
     */
    public void checkWin(){
        boolean allAbsorbed = true;
        for(Ball b: balls){
            if(!b.IsAbsorbed()){
                allAbsorbed=false;
                break;
            }
        }
        if(allAbsorbed){
            levelUp = true;
            gameOver = true;
            totalScore += levelScore;
            levelScore = 0;
            // Stop the game for score conversion
            if(!paused){
                pause();
            }
            // Update the remaining time
            if(remainingTime == -1){
                remainingTime = millis() - starttime;
            } else if (remainingTime <= 0){
                // When the calculation is done, update the game
                if(currentLevel + 1 >= totalLevel){
                    gameWin = true;
                } else {
                    currentLevel += 1;
                    reset();
                }
            }
        }
    }

   /**
     * Resets the game to its initial state or to the beginning of the current level.
     *
     * <p>This method reinitialises the game components like balls, holes, and walls, and resets
     * game states such as score and time.
     */
    public void reset(){
        if(gameWin){
            currentLevel = 0;
        }
        // Reset the Balls, Walls and Holes
        balls = new ArrayList<Ball>();
        holes = new ArrayList<Hole>();
        walls = new ArrayList<Wall>();
        spawners = new ArrayList<Spawner>();
        ballQueue = new LinkedList<Ball>();
        ballsOnBoard = new ArrayList<Ball>();
        lines = new ArrayList<ArrayList<Point>>();

        // Reset the game attributes
        starttime = millis();
        lastSpawnTime = starttime;
        gameOver = false;
        gameWin = false;
        levelScore = 0;
        paused = false;
        remainingTime = -1;
        levelUp = false;
        rotateBlock = new ArrayList<Wall>();
        rotateBlock.add(new Wall(0,App.TOPBAR,4,false));
        rotateBlock.add(new Wall(App.WIDTH - App.CELLSIZE,App.HEIGHT - App.CELLSIZE,4,false));
        numFrameForConversion = (int)0.067 * App.FPS;
        remainFrameForConversion = 0;


        // Clean & Reset the board
        for (int rowNum = 0; rowNum < App.BOARD_HEIGHT; rowNum++){
            for (int colNum = 0; colNum < App.BOARD_WIDTH; colNum++){
                board[rowNum][colNum] = new Cell(
                    colNum * App.CELLSIZE,
                    rowNum * App.CELLSIZE + App.TOPBAR);                
            }
        }

        // Load the balls queue of the current level
        for(String ballstr: ballsRaw.get(currentLevel)){
            int color = 0;
            switch(ballstr){
                case "grey":
                    color = 0;
                    break;
                case "orange":
                    color = 1;
                    break;
                case "blue":
                    color = 2;
                    break;
                case "green":
                    color = 3;
                    break;
                case "yellow":
                    color = 4;
                    break;
            }
            Ball newBall = new Ball(-10, -10,color);
            balls.add(newBall);
            ballQueue.add(newBall);
        }
        
        // Read the gameboard of the current level
        for (int rowNum = 0; rowNum < App.BOARD_HEIGHT ; rowNum++){
            // If the next row is not empty
            if(rowNum < layouts[currentLevel].length && layouts[currentLevel][rowNum] != null){
                for(int colNum = 0; colNum < App.BOARD_WIDTH; colNum++){
                    // If the next character in line is not empty
                    if (colNum < layouts[currentLevel][rowNum].trim().length()){
                        switch(layouts[currentLevel][rowNum].charAt(colNum)){
                            case ' ':
                                if(board[rowNum][colNum].getGameObject() == null){
                                    board[rowNum][colNum].setGameObject(null);
                                }
                                break;
                            case 'X':
                                Wall newWall = new Wall(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR,
                                    0,false);
                                board[rowNum][colNum].setGameObject(newWall);;
                                walls.add(newWall);
                                break;                                    
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                                int c1 = layouts[currentLevel][rowNum].charAt(colNum) - '0';    
                                Wall newWall1 = new Wall(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR,
                                    c1,false);
                                board[rowNum][colNum].setGameObject(newWall1);;
                                walls.add(newWall1);
                                break;
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                int c4 = layouts[currentLevel][rowNum].charAt(colNum) - '5';    
                                Wall newBrick = new Wall(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR,
                                    c4,true);
                                board[rowNum][colNum].setGameObject(newBrick);;
                                walls.add(newBrick);
                                break;
                            case 'S':
                                Spawner newSpawner = new Spawner(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR);
                                board[rowNum][colNum].setGameObject(newSpawner);
                                spawners.add(newSpawner);
                                break;
                            case 'H':
                                int c2 = layouts[currentLevel][rowNum].charAt(colNum+1) - '0';
                                Hole newHole = new Hole(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR,
                                    c2);
                                board[rowNum][colNum].setGameObject(newHole);
                                board[rowNum][colNum+1].setGameObject(newHole);
                                board[rowNum+1][colNum].setGameObject(newHole);
                                board[rowNum+1][colNum+1].setGameObject(newHole);
                                holes.add(newHole);
                                colNum++;
                                break;
                            case 'B':
                                int c3 = layouts[currentLevel][rowNum].charAt(colNum+1) - '0';
                                board[rowNum][colNum].setGameObject(null);
                                board[rowNum][colNum+1].setGameObject(null);
                                Ball newBall = new Ball(
                                    colNum * App.CELLSIZE,
                                    rowNum * App.CELLSIZE + App.TOPBAR,
                                    c3);
                                balls.add(newBall);
                                ballsOnBoard.add(newBall);
                                newBall.randomVelocity();
                                colNum++;
                                break;
                        }
                    } else {
                        // If the next character in line is empty
                        board[rowNum][colNum].setGameObject(null);
                    }
                }
            } else {
                // If the next row is empty
                for(int colNum = 0; colNum < App.BOARD_WIDTH;colNum++){
                    board[rowNum][colNum].setGameObject(null);
                }
            }
        }
    }

    // Pre-defined scaffold
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(App.WIDTH, App.HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(App.FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
        // Load the JSON file
        JSONObject json = loadJSONObject(configPath);
        JSONArray levelsArray = json.getJSONArray("levels");

        // Load the levels' attributes from the config JSON file
        totalLevel = levelsArray.size();
        layoutDir = new String[totalLevel];
        time = new int[totalLevel];
        spawn_interval = new int[totalLevel];
        score_increase_from_hole_capture_modifier = new float[totalLevel];
        score_decrease_from_wrong_hole_modifier = new float[totalLevel];
        ballsRaw = new ArrayList<ArrayList<String>>(totalLevel);
        for (int i = 0; i < totalLevel; i++){
            JSONObject leveljson = levelsArray.getJSONObject(i);
            layoutDir[i] = leveljson.getString("layout");
            time[i] = leveljson.getInt("time");
            spawn_interval[i] = leveljson.getInt("spawn_interval");
            score_increase_from_hole_capture_modifier[i] = leveljson.getFloat("score_increase_from_hole_capture_modifier");
            score_decrease_from_wrong_hole_modifier[i] = leveljson.getFloat("score_decrease_from_wrong_hole_modifier");
            JSONArray ballArray = leveljson.getJSONArray("balls");
            ArrayList<String> ballsSeries = new ArrayList<String>();
            for(int j = 0; j < ballArray.size(); j++){
                ballsSeries.add(ballArray.getString(j));
            }
            ballsRaw.add(ballsSeries);
        }

        // Load the balls attributes from config JSON file
        JSONObject scoreIncreaseJSON = json.getJSONObject("score_increase_from_hole_capture");
        score_increase_from_hole_capture = new HashMap<>();
        for (Object keyObj : scoreIncreaseJSON.keys()) {
            Integer key = -1;
            switch((String)keyObj){
                case "grey":
                    key = 0;
                    break;
                case "orange":
                    key = 1;
                    break;
                case "blue":
                    key = 2;
                    break;
                case "green":
                    key = 3;
                    break;
                case "yellow":
                    key = 4;
                    break;
            }
            score_increase_from_hole_capture.put(key, scoreIncreaseJSON.getInt((String)keyObj));
        }
        JSONObject scoreDecreaseJSON = json.getJSONObject("score_decrease_from_wrong_hole");
        score_decrease_from_wrong_hole = new HashMap<>();
        for (Object keyObj : scoreDecreaseJSON.keys()) {
            Integer key = -1;
            switch((String)keyObj){
                case "grey":
                    key = 0;
                    break;
                case "orange":
                    key = 1;
                    break;
                case "blue":
                    key = 2;
                    break;
                case "green":
                    key = 3;
                    break;
                case "yellow":
                    key = 4;
                    break;
            }
            score_decrease_from_wrong_hole.put(key, scoreDecreaseJSON.getInt((String)keyObj));
        }

        // Load the levels into the attribute
        layouts = new String[totalLevel][App.BOARD_HEIGHT];
        for (int i = 0; i < totalLevel ; i++){
            try {
                File f = new File(layoutDir[i]);
                Scanner scan = new Scanner(f);
                int j = 0;
                while(scan.hasNext() && j < App.BOARD_HEIGHT){
                    layouts[i][j] = scan.nextLine();
                    j++;
                }
                scan.close();
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }

        // Set the current level
        currentLevel = 0;

		// the image is loaded from relative path: "src/main/resources/inkball/..."
        // Pre-load the images to the App
        loadSprite("ball", 5);
        loadSprite("entrypoint", 1);
        loadSprite("hole", 5);
        loadSprite("inkball_spritesheet", 1);
        loadSprite("tile", 1);
        loadSprite("wall", 5);
        loadSprite("brick", 5);

        // Use reset() to set up the game attributes
        reset();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        // Restart the game if R / r is pressed
        if(key == 'R' || key == 'r'){
            reset();
        } 
        // Pause / Resume the game if space is pressed 
        else if(key == ' '){
            pause();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        if(mouseButton == LEFT){
            ArrayList<Point> newLine = new ArrayList<Point>();
            lines.add(newLine);
        }
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		if(mouseButton == LEFT){
            Point newPoint = new Point(mouseX,mouseY);
            if(lines.size()>0){
                lines.get(lines.size()-1).add(newPoint);
            }
        }
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
        if(mouseButton == RIGHT){
            boolean hitted = false;
            for(ArrayList<Point> line: lines){
                for(Point point: line){
                    if(mouseX > point.getXF() - App.POINTSIZE/2 && mouseX < point.getXF() + App.POINTSIZE/2 && mouseY > point.getYF() - App.POINTSIZE/2 && mouseY < point.getYF() + App.POINTSIZE/2){
                        hitted = true;
                        break;
                    }
                }
                if(hitted){
                    lines.remove(line);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		if(mouseButton == LEFT && lines.size()>0){
            if(lines.get(lines.size()-1).size()==0){
                lines.remove(lines.get(lines.size()-1));
            }
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
        // Clean the background
        background(123);

        //----------------------------------
        //Update the movement of balls if it is not paused
        //----------------------------------
        if (!paused){
            for(Ball b: ballsOnBoard){
                b.updatePos();
                b.beingAbsorbed = false;
                b.lineCollided = false;
                b.wallCollided = false;
                for(ArrayList<Point> line: lines){
                    if(b.checkLineCollision(line)){
                        lines.remove(line);
                        break;
                    }
                }
            }
            // Check if the next cell has walls, if yes trigger the collision with wall
            for(Wall w: walls){
                for(Ball b:ballsOnBoard){
                    if(!b.wallCollided){
                        w.checkCollision(b,this);
                    }
                }
            }
            // Check if the bricks need to be removed because of too many hits
            ArrayList<Wall> bricksToRemove = new ArrayList<Wall>();
            for(Wall w:walls){
                if(w.checkBrickBroken()){
                    bricksToRemove.add(w);
                }
            }
            for(Wall brick:bricksToRemove){
                int xIndex = brick.getX()/App.CELLSIZE;
                int yIndex = (brick.getY()-App.TOPBAR)/App.CELLSIZE;
                walls.remove(brick);
                board[yIndex][xIndex].setGameObject(null);
            }

            // Check if the next cell has holes, if yes trigger the collision with holes
            for(Hole h: holes){
                for(Ball b: balls){
                    if(ballsOnBoard.contains(b)){
                        h.checkAbsorb(b, this);
                    }
                }
            }
            
            // Spawn the balls in the queue
            Random random = new Random();
            if(millis() - lastSpawnTime >= spawn_interval[currentLevel] * 1000 && (!ballQueue.isEmpty())){
                int index = random.nextInt(spawners.size());
                spawners.get(index).spawn(ballQueue, ballsOnBoard);
                lastSpawnTime = millis();
            }
        }

        // Check Win
        checkWin();
        //----------------------------------
        //display Board for current level:
        //----------------------------------
        // Display the cells
        for (Cell []row: board){
            for(Cell c: row){
                c.draw(this);
            }
        }
        
        // Display the walls
        for (Wall wall: walls){
            wall.draw(this);
        }
        // Display the holes
        for (Hole hole: holes){
            hole.draw(this);
        }
        // Display the spawners
        for (Spawner spawner: spawners){
            spawner.draw(this);
        }
        //Display the lines
        for (ArrayList<Point> line: lines){
            for(Point point: line){
                fill(0);
                noStroke();
                ellipse(point.getXF(),point.getYF(),App.POINTSIZE,App.POINTSIZE);
            }
        }
        // Display the balls
        for (Ball ball: balls){
            ball.draw(this);
        }

        // Display the unspawned balls
        fill(0);
        rect(App.CELLSIZE/2,(App.TOPBAR - App.CELLSIZE)/2,5 * App.CELLSIZE, App.CELLSIZE);
        int displayedQueue = 0;
        for (Ball ball: ballQueue){
            if(displayedQueue > 4){
                break;
            }
            ball.drawUnspawned(this, displayedQueue * App.CELLSIZE + App.CELLSIZE - App.BALLSIZE/2, (App.TOPBAR - App.BALLSIZE)/2);
            displayedQueue ++;
        }

        // Display the countdown to the next spawn
        if(!ballQueue.isEmpty()){ 
            fill(0);
            textSize(16);
            textAlign(LEFT,CENTER);
            float timeToNextSpawn = 1f;
            if(paused){
                timeToNextSpawn = ((float)(spawn_interval[currentLevel] * 1000 - pausedTime + lastSpawnTime)) / 1000f;
            } else {
                timeToNextSpawn = ((float)(spawn_interval[currentLevel] * 1000 - millis() + lastSpawnTime)) / 1000f;
            }
            text(String.format("%.1f",timeToNextSpawn),6 * App.CELLSIZE,App.TOPBAR/2);
        }


        // Score conversion and roate teh yellow blocks if the level is finished
        // if the game ends, calculate the conversion
        if(levelUp && remainingTime >= 0){
            if(remainFrameForConversion - 1 <= 0){
                remainingTime -=1000;
                totalScore += 1;
                // Move the flashing wall
                for(Wall w:rotateBlock){
                    w.rotate();
                }
                remainFrameForConversion = numFrameForConversion;
            } else {
                remainFrameForConversion -= 1;
            }
            for(Wall w:rotateBlock){
                w.draw(this);
            }
        }
        //----------------------------------
        //display score & time
        //----------------------------------
        fill(0);
        textSize(16);
        textAlign(RIGHT,BOTTOM);
        text("Score: " + String.valueOf(totalScore + levelScore), App.WIDTH-(App.CELLSIZE/2),App.TOPBAR/2);
        // Show the time depending on whether the time is already up
        if(!paused){
            if(time[currentLevel] <= (millis() - starttime)/1000){
                gameOver = true;
                text("Time: " + 0, App.WIDTH-(App.CELLSIZE/2),App.TOPBAR);
            } else {
                text("Time: " + String.valueOf(time[currentLevel] - (millis() - starttime)/1000), App.WIDTH-(App.CELLSIZE/2),App.TOPBAR);
            }
        } 
        // If the game is paused
        else {
            if(levelUp){
                text("Time: " + remainingTime/1000, App.WIDTH-(App.CELLSIZE/2),App.TOPBAR);
            }
            else if(time[currentLevel] <= (pausedTime - starttime)/1000){
                gameOver = true;
                text("Time: " + 0, App.WIDTH-(App.CELLSIZE/2),App.TOPBAR);
            } else {
                text("Time: " + String.valueOf(time[currentLevel] - (pausedTime - starttime)/1000), App.WIDTH-(App.CELLSIZE/2),App.TOPBAR);
            }
        }
        
		//----------------------------------
        //display game end message or game paused message
        //----------------------------------
        if(gameWin){
            fill(0);
            textSize(16);
            textAlign(CENTER,CENTER);
            text("=== ENDED ===",App.WIDTH/2 + App.CELLSIZE,App.TOPBAR/2);
            if(!paused){
                paused = true;
                pausedTime = millis();
            }
        }
        else if (gameOver && (!levelUp) && (time[currentLevel] <= (millis() - starttime)/1000)){
            fill(0);
            textSize(16);
            textAlign(CENTER,CENTER);
            text("=== TIME'S UP ===",App.WIDTH/2 + App.CELLSIZE,App.TOPBAR/2);
            if(!paused){
                paused = true;
                pausedTime = millis();
            }
        } 
        else if (paused && (!levelUp)){
            fill(0);
            textSize(16);
            textAlign(CENTER,CENTER);
            text("*** PAUSED ***",App.WIDTH/2 + App.CELLSIZE,App.TOPBAR/2);
        }
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
