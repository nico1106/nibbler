package nibbler;


import nibbler.actions.Collision;
import nibbler.actions.FileHandler;
import nibbler.game.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Nibbler {
    private static final int ROWS = 30;
    private static final int COLUMNS = 40;
    private static final boolean MOUSE = false;
    private static final String SCORE_FORMAT = "%1$05d";
    private static final String APPLE_COUNTER_FORMAT = "%1$03d";
    private static final String HIGHSCORE_FILE = "nibbler_highscore.txt";
    private static final String AUTHOR = "NICO DIMISIANOS";


    private boolean play = true;
    private boolean moveForward = true;

    ArrayList<Level> levels;
    GameView gameView;
    Snake snake;
    Apple apple;
    Collision collision;
    Wall wall;
    Random random;
    Player player;

    private long lastDisplayed, lastMoveSnake;

    public static void main(String[] args) { new Nibbler().play(); }

    private Nibbler() {
        gameView = new GameView(ROWS, COLUMNS, GameView.WINDOWSIZE_LARGE);
        setMetadata();
        initGame();
    }

    private void initGame() {
        snake = new Snake();
        apple = new Apple();
        collision = new Collision();
        levels = new ArrayList<Level>();
        levels.add(new Level(1, 3, 300, wallDesign.WALL_LEVEL_1, 3000));
        levels.add(new Level(2, 6, 220, wallDesign.WALL_LEVEL_2, 4500));
        levels.add(new Level(3, 7, 150, wallDesign.WALL_LEVEL_3, 6000));
        player = new Player();
        lastDisplayed = 0;
        lastMoveSnake = 0;
    }

    private void play() {
        showStartScreen();
        gameView.stopAllSounds();
        gameView.playSound("arcade_sound.wav", true);

        // set colormap, especially to display the colors of the snake
        HashMap<Character, Color> colorMap = new HashMap<>(Map.of('B', Color.BLACK,'R', Color.RED, 'G', Color.GREEN));
        gameView.setColormap(colorMap);

        for(Level level: levels) {
            showLevelScreen(level);
            gameView.changeResolution(ROWS, COLUMNS);
            play = true;
            while (play) {
                try { Thread.sleep(5); } catch (InterruptedException ignore) {}
                // Evaluate User Inputs
                evaluateKeys();
                // Calculate Matchfield
                calculateMatchfield(level);

                long now = System.currentTimeMillis();
                if (now - lastDisplayed >= 15) {
                    gameView.printCanvas();
                    lastDisplayed = now;
                    level.setBonus((level.getBonus()-1));
                }

            }

            checkScoreAndWriteToFile();
            // check if we are at the end level and when yes, show the winner screen
            if(level.getNumber()==levels.size()) {
                showWinnerScreen();
            }
        }
    }

    private void calculateMatchfield(Level level) {
        long now = System.currentTimeMillis();
        if (now - lastMoveSnake >= level.getTact()) {
            moveSnake(snake.head.getDirection());
            lastMoveSnake = now;
        }
        gameView.clearCanvas();
        // add snake with tails, apple, player name, current score and highscore to the gameView
        addItemsToCanvas(level);
        // do all the checks, e.g. if snake eats the apple or if snake collides with itself
        doChecks(level);
    }

    private void showStartScreen() {
        gameView.changeResolution(20, 35);
        gameView.stopAllSounds();
        gameView.playSound("loading_screen_sound.wav", true);
        gameView.addToCanvas("Welcome to Nibbler!", 1, 7, Color.ORANGE);
        String message = "\n\n\n\nPlease enter your name: ";
        String name = gameView.getStringFromUser(message, 10);
        gameView.addToCanvas("© 2020 | Nico Dimisianos (701688)", 19, 1, Color.CYAN);
        gameView.printCanvas();

        name = name.trim().toLowerCase();
        // check if name is valid, otherwise start the StartScreen again
        if (name == null || name == "" ) {
            // Init a new Game and go to the StartScreen
            initGame();
            play();
        }
        player.setName(name);
        player.setHighscore(FileHandler.getHighscoreFromFile(player));
        gameView.addToCanvasCentered("Please hit --> ENTER <--");
        gameView.printCanvas();

        while(true) {
            KeyEvent[] keyEvents = gameView.getKeyEvents();
            for (KeyEvent keyEvent : keyEvents) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                        return;
                    }
                }
            }
            try { Thread.sleep(2); } catch (InterruptedException ignore) {}
        }

    }

    private void evaluateKeys(){
        KeyEvent[] keyEvents = gameView.getKeyEvents();
        for (KeyEvent keyEvent : keyEvents) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) { /*move snake left*/
                if (!(snake.head.getDirection() == Head.Direction.RIGHT) && !(snake.head.getDirection() == Head.Direction.LEFT) && !snake.waitToMove) {
                    moveSnake(Head.Direction.LEFT);
                }
                break;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_UP) { /*move snake up*/
                if (!(snake.head.getDirection() == Head.Direction.DOWN) && !(snake.head.getDirection() == Head.Direction.UP) && !snake.waitToMove) {
                    moveSnake(Head.Direction.UP);
                }
                break;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) { /*move snake right*/
                if(!(snake.head.getDirection() == Head.Direction.LEFT) && !(snake.head.getDirection() == Head.Direction.RIGHT) && !snake.waitToMove) {
                    moveSnake(Head.Direction.RIGHT);
                }
                break;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) { /*move snake down*/
                if(!(snake.head.getDirection() == Head.Direction.UP) && !(snake.head.getDirection() == Head.Direction.DOWN) && !snake.waitToMove) {
                    moveSnake(Head.Direction.DOWN);
                }
                break;
            }
        }
    }

    private void setMetadata() {
        gameView.setTitle("Nibbler");
        gameView.setStatusText("Java Programmierung SS 2020");
        gameView.setWindowIcon("nibbler_icon.png");
        gameView.setDefaultBackgroundColor(Color.BLACK);
    }

    private void setRandomPointsToApple(Apple apple)  {
        random = new Random();
        int x = random.nextInt(Nibbler.getColumns());
        int y = random.nextInt((Nibbler.getRows()-1)-10+1)+10;
        // make sure that the apple won't be spawned on the wall or on the snake, otherwise repeat the process
        if (gameView.getCharacter(y, x) == 'X' || gameView.getCharacter(y, x) == 'R' || gameView.getCharacter(y, x) == 'G') {
            setRandomPointsToApple(apple);
        } else {
            apple.setX(x);
            apple.setY(y);
        }
    }

    private boolean checkMoveForward(Head.Direction direction){
        moveForward = true;
        boolean changeDirection = true;
        switch(direction) {
            case UP:
                if (gameView.getCharacter(snake.head.getY()-1, snake.head.getX()) == 'X') {
                   changeDirection = false;
                }
                break;
            case RIGHT:
                if (gameView.getCharacter(snake.head.getY(), snake.head.getX()+1) == 'X') {
                    changeDirection = false;
                }
                break;
            case DOWN:
                if (gameView.getCharacter(snake.head.getY()+1, snake.head.getX()) == 'X') {
                    changeDirection = false;
                }
                break;
            case LEFT:
                if (gameView.getCharacter(snake.head.getY(), snake.head.getX()-1) == 'X') {
                    changeDirection = false;
                }
                break;
        }
        return changeDirection;
    }

    private void moveSnake(Head.Direction direction) {
        if(checkMoveForward(direction)) {
            snake.head.setDirection(direction);
            snake.waitToMove = true;
            snake.move();
            snake.waitToMove = false; }
    }

    private void playLoserSound() {
        gameView.stopAllSounds();
        gameView.playSound("lose_sound.wav", false);
        try { Thread.sleep(3000); } catch (InterruptedException ignore) {}
    }

    private void showLoserScreen() {
        gameView.clearCanvas();
        gameView.changeResolution(20, 35);
        gameView.addToCanvasCentered("You've lost!");
        gameView.printCanvas();
        try { Thread.sleep(7000); } catch (InterruptedException ignore) {}
    }

    private void resetSnakeAndApple() {
        snake = new Snake();
        apple = new Apple();
    }

    private void showLevelScreen(Level level){
        int wait = 5;
        gameView.changeResolution(10, 20);
        for(int i=1;i<=5;i++) {
            gameView.clearCanvas();
            gameView.addToCanvas("LEVEL " + level.getNumber(), 1, 7, Color.ORANGE);
            gameView.addToCanvas("Get ready", 4, 6);
            gameView.addToCanvasCentered("\n\n" + wait, Color.CYAN);
            gameView.printCanvas();
            try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
            wait-=1;
        }

    }

    private void showWinnerScreen(){
        gameView.stopAllSounds();
        gameView.clearCanvas();
        try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
        gameView.changeResolution(10, 40);
        gameView.addToCanvas("You are a HERO!", 1, 12, Color.ORANGE);
        gameView.addToCanvas("You mastered the HARD Nibbler game", 3, 3);
        gameView.addToCanvas("made by", 5, 16);
        gameView.addToCanvas(Nibbler.AUTHOR, 8, 12, Color.CYAN);
        gameView.printCanvas();
        gameView.playSound("wedding_applause_sound.wav", false);
        try { Thread.sleep(10000); } catch (InterruptedException ignore) {}
        // Init a new Game and go to the StartScreen
        initGame();
        play();
    }

    private void checkScoreAndWriteToFile(){
        if(player.getCurrentscore() > player.getHighscore()) {
            FileHandler.writeScoreToFile(player);
        }
    }

    private void doChecks(Level level) {
        // check if apple was reseted and spawn it into the matchfield
        if (apple.getX() == -1 && apple.getY() == -1 && level.getCountApple() != level.getCountAppleEaten()) {
            setRandomPointsToApple(apple);
        }

        // check if we have eaten enough apples to get to the next level
        if(level.getCountAppleEaten() == level.getCountApple()) {
            play = false;
            gameView.clearCanvas();
            if (level.getBonus() > 0) {
                player.setCurrentscore(player.getCurrentscore() + level.getBonus());
            }
            addItemsToCanvas(level);
            try { Thread.sleep(16); } catch (InterruptedException ignore) {}
            gameView.printCanvas();
            try { Thread.sleep(2000); } catch (InterruptedException ignore) {}
            resetSnakeAndApple();

        }

        // check if snake eats the apple
        if(collision.collideWithApple(snake, apple)) {
            gameView.playSound("apple_bite_sound.wav", false);
            level.setCountAppleEaten(level.getCountAppleEaten()+1);
            apple.reset();
            snake.addTail();
            player.setCurrentscore(player.getCurrentscore()+10);
        };

        // check if collision with self (snake) exists
        if(collision.collideWithSelf(snake)){
            lostAndExitGame();
        };
        // check if bonus = 0, then exit the game
        if (level.getBonus() == 0) {
            gameView.clearCanvas();
            addItemsToCanvas(level);
            gameView.printCanvas();
            lostAndExitGame();
        }
    }

    private void addItemsToCanvas(Level level) {
        // add wall to gameView
        gameView.addToCanvas(level.getWall().getToken(), 10, 0, Color.CYAN);

        gameView.addColorStringToCanvas(snake.head.toString(), snake.head.getY(), snake.head.getX());
        gameView.addToCanvas(apple.toString(), apple.getY(), apple.getX(), Color.YELLOW);
        gameView.addToCanvas("Player: "+ player.getName(), 1, 1);
        gameView.addToCanvas(" Level: "+ level.getNumber(), 2, 1, Color.ORANGE);
        gameView.addToCanvas("Highscore: " + String.format(SCORE_FORMAT, player.getHighscore()), 1, COLUMNS-17 );
        gameView.addToCanvas("Score: " + String.format(SCORE_FORMAT, player.getCurrentscore()), 2, COLUMNS-13, Color.ORANGE);
        gameView.addToCanvas("Target:    " + String.format(APPLE_COUNTER_FORMAT, level.getCountApple()), 4, COLUMNS-15);
        gameView.addToCanvas("Current:    " + String.format(APPLE_COUNTER_FORMAT, level.getCountAppleEaten()), 5, COLUMNS-16, Color.RED);
        gameView.addToCanvas("Bonus: " + String.format(SCORE_FORMAT, level.getBonus()), 8, COLUMNS-26, Color.GREEN);

        if (snake.tails.size() >= 1) {
            for (Tail tail : snake.tails){
                gameView.addColorStringToCanvas(tail.toString(), tail.getY(), tail.getX());
            }
        }
    }

    private void lostAndExitGame() {
        play = false;
        playLoserSound();
        // Write Score to file
        checkScoreAndWriteToFile();
        showLoserScreen();
        // Init a new Game and go to the StartScreen
        initGame();
        play();
    }

    public static String getHighscoreFile() { return HIGHSCORE_FILE; }
    public static int getRows() { return ROWS; }
    public static int getColumns() {return COLUMNS; }

}
