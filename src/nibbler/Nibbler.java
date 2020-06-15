package nibbler;

import nibbler.actions.Collision;
import nibbler.actions.FileHandler;
import nibbler.game.*;
import nibbler.ui.GameView;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Nibbler {
    private static final int ROWS = 30, COLUMNS = 40;
    private static final boolean MOUSE = false;
    private static final String SCORE_FORMAT = "%1$05d", APPLE_COUNTER_FORMAT = "%1$03d";
    private static final String HIGHSCORE_FILE = "nibbler_highscore.txt", AUTHOR = "NICO DIMISIANOS", AUTHOR_NUMBER = "701688";

    private boolean play = true;

    private ArrayList<Level> levels;
    private GameView gameView;
    private Snake snake;
    private Apple apple;
    private Collision collision;
    private Random random;
    private Player player;

    private long lastDisplayed, lastMoveSnake;

    /**
     * Start the game.
     * @param args no parameters.
     */
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
        levels.add(new Level(2, 6, 220, WallDesign.WALL_LEVEL_2, 5000));
        levels.add(new Level(1, 3, 300, WallDesign.WALL_LEVEL_1, 3500));
        levels.add(new Level(3, 7, 150, WallDesign.WALL_LEVEL_3, 6500));
        // sort levels array list based on level number ASC
        levels.sort((a, b) -> { return a.getNumber() - b.getNumber(); });
        player = new Player();
        lastDisplayed = 0;
        lastMoveSnake = 0;
    }

    private void play() {
        showStartScreen();
        gameView.stopAllSounds();
        gameView.playSound("arcade_sound_shorter.wav", true);
        // set colormap, especially to display the colors of the snake
        HashMap<Character, Color> colorMap = new HashMap<>(Map.of('B', Color.BLACK, 'R', Color.RED, 'G', Color.GREEN));
        gameView.setColormap(colorMap);
        for (Level level : levels) {
            showLevelScreen(level);
            gameView.changeResolution(ROWS, COLUMNS);
            play = true;
            while (play) {
                try { Thread.sleep(5); } catch (InterruptedException ignore) {}
                // evaluate User Input
                evaluateKeys();
                // calculate matchfield
                calculateMatchfield(level);
                long now = System.currentTimeMillis();
                // print canvas every 16ms
                if (now - lastDisplayed >= 15) {
                    gameView.printCanvas();
                    lastDisplayed = now;
                    level.setBonus((level.getBonus() - 1));
                }
            }
            checkScoreAndWriteToFile();
            // check if we are at the end level and when yes, show the winner screen
            if (level.getNumber() == levels.size()) showWinnerScreen();
        }
    }

    private void calculateMatchfield(Level level) {
        long now = System.currentTimeMillis();
        if (now - lastMoveSnake >= level.getTact()) {
            moveSnake(snake.head.getDirection());
            lastMoveSnake = now;
        }
        gameView.clearCanvas();
        addItemsToCanvas(level);
        doChecks(level);
    }

    private void showStartScreen() {
        gameView.changeResolution(40, 40);
        HashMap<Character, Color> colorMap = new HashMap<>(Map.of('B', Color.BLACK, 'W', Color.WHITE, 'H', new Color(107, 176, 66), 'P', Color.PINK, 'G', new Color(48, 102, 69)));
        gameView.setColormap(colorMap);
        gameView.addColorStringToCanvas(snake.toString(), 2, 11);
        gameView.printCanvas();
        gameView.stopAllSounds();
        gameView.playSound("loading_screen_sound.wav", true);
        gameView.addToCanvas("NIBBLER", 20, 16, Color.ORANGE);
        String message = "\n\n\n    Please enter your name: ";
        String name = gameView.getStringFromUser(message, 10);
        gameView.addToCanvas("© 2020 | " + AUTHOR + " (" + AUTHOR_NUMBER + ")", 38, 3, Color.CYAN);
        gameView.printCanvas();
        name = name.trim().toLowerCase();
        if (name == null || name.equals("")) { initGame(); play(); }
        player.setName(name);
        player.setHighscore(FileHandler.getHighscoreFromFile(player));
        gameView.addToCanvas("Please hit <<< ENTER >>>", 29, 8);
        gameView.printCanvas();

        while (true) {
            KeyEvent[] keyEvents = gameView.getKeyEvents();
            for (KeyEvent keyEvent : keyEvents) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) return;
                }
            }
            try { Thread.sleep(2); } catch (InterruptedException ignore) {}
        }

    }

    private void evaluateKeys() {
        KeyEvent[] keyEvents = gameView.getKeyEvents();
        for (KeyEvent keyEvent : keyEvents) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) { /*move snake left*/
                if (!(Snake.head.getDirection() == Head.Direction.RIGHT) && !(Snake.head.getDirection() == Head.Direction.LEFT) && !snake.getWaitToMove()) moveSnake(Head.Direction.LEFT);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_UP) { /*move snake up*/
                if (!(Snake.head.getDirection() == Head.Direction.DOWN) && !(Snake.head.getDirection() == Head.Direction.UP) && !snake.getWaitToMove()) moveSnake(Head.Direction.UP);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) { /*move snake right*/
                if (!(Snake.head.getDirection() == Head.Direction.LEFT) && !(Snake.head.getDirection() == Head.Direction.RIGHT) && !snake.getWaitToMove()) moveSnake(Head.Direction.RIGHT);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) { /*move snake down*/
                if (!(Snake.head.getDirection() == Head.Direction.UP) && !(Snake.head.getDirection() == Head.Direction.DOWN) && !snake.getWaitToMove()) moveSnake(Head.Direction.DOWN);
            }
        }
    }

    private void setMetadata() {
        gameView.setTitle("Nibbler");
        gameView.setStatusText("Nibbler");
        gameView.setWindowIcon("nibbler_icon.png");
        gameView.setDefaultBackgroundColor(Color.BLACK);
    }

    private void setRandomPointsToApple(Apple apple) {
        random = new Random();
        int x = random.nextInt(Nibbler.getColumns());
        int y = random.nextInt((Nibbler.getRows() - 1) - 10 + 1) + 10;
        if (gameView.getCharacter(y, x) == 'X' || checkPointsWithSnake(y, x) ){
            setRandomPointsToApple(apple);
        } else {
            apple.setX(x); apple.setY(y);
        }
    }

    private boolean checkPointsWithSnake(int y, int x){
        for(int i=0;i<snake.tails.size();i++){
            if(snake.tails.get(i).getX() == x && snake.tails.get(i).getY() == y) return true;
        }
        if (snake.head.getX() == x && snake.head.getY() == y) return true;
        return false;
    }

    private boolean checkMoveForward(Head.Direction direction) {
        boolean changeDirection = true;
        switch (direction) {
            case UP:
                if (gameView.getCharacter(Snake.head.getY() - 1, Snake.head.getX()) == 'X') changeDirection = false;
                break;
            case RIGHT:
                if (gameView.getCharacter(Snake.head.getY(), Snake.head.getX() + 1) == 'X') changeDirection = false;
                break;
            case DOWN:
                if (gameView.getCharacter(Snake.head.getY() + 1, Snake.head.getX()) == 'X') changeDirection = false;
                break;
            case LEFT:
                if (gameView.getCharacter(Snake.head.getY(), Snake.head.getX() - 1) == 'X') changeDirection = false;
                break;
        }
        return changeDirection;
    }

    private void moveSnake(Head.Direction direction) {
        if (checkMoveForward(direction)) {
            Snake.head.setDirection(direction);
            snake.setWaitToMove(true);
            snake.move();
            snake.setWaitToMove(false);
        }
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

    private void showLevelScreen(Level level) {
        gameView.changeResolution(10, 20);
        for (int i = 5; i >= 1; i--) {
            gameView.clearCanvas();
            gameView.addToCanvas("LEVEL " + level.getNumber(), 1, 7, Color.ORANGE);
            gameView.addToCanvas("Get ready", 4, 6);
            gameView.addToCanvasCentered("\n\n" + i, Color.CYAN);
            gameView.printCanvas();
            try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
        }
    }

    private void showWinnerScreen() {
        gameView.stopAllSounds();
        gameView.clearCanvas();
        try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
        gameView.changeResolution(10, 40);
        gameView.addToCanvas("You are a HERO!", 1, 12, Color.ORANGE);
        gameView.addToCanvas("You mastered the Nibbler game", 3, 5);
        gameView.addToCanvas("made by", 5, 16);
        gameView.addToCanvas(Nibbler.AUTHOR, 8, 12, Color.CYAN);
        gameView.printCanvas();
        gameView.playSound("wedding_applause_sound.wav", false);
        try { Thread.sleep(10000); } catch (InterruptedException ignore) {}
        initGame();
        play();
    }

    private void checkScoreAndWriteToFile() {
        if (player.getCurrentscore() > player.getHighscore()) {
            FileHandler.writeScoreToFile(player);
        }
    }

    private void doChecks(Level level) {
        // check if apple was reset and spawn it into the matchfield
        if (apple.getX() == -1 && apple.getY() == -1 && level.getCountApple() != level.getCountAppleEaten()) setRandomPointsToApple(apple);
        // check if we have eaten enough apples to get to the next level
        if (level.getCountAppleEaten() == level.getCountApple()) {
            play = false;
            gameView.clearCanvas();
            if (level.getBonus() > 0) player.setCurrentscore(player.getCurrentscore() + level.getBonus());
            addItemsToCanvas(level);
            try { Thread.sleep(16); } catch (InterruptedException ignore) {}
            gameView.printCanvas();
            try { Thread.sleep(2000); } catch (InterruptedException ignore) {}
            resetSnakeAndApple();
        }
        // check if snake eats the apple
        if (collision.collideWithApple(snake, apple)) {
            gameView.playSound("apple_bite_sound.wav", false);
            level.setCountAppleEaten(level.getCountAppleEaten() + 1);
            apple.reset();
            snake.addTail();
            player.setCurrentscore(player.getCurrentscore() + 10);
        }
        // check if collision with self (snake) exists
        if (collision.collideWithSelf(snake)) lostAndExitGame();
        // check if bonus = 0, then exit the game
        if (level.getBonus() == 0) {
            gameView.clearCanvas();
            addItemsToCanvas(level);
            gameView.printCanvas();
            lostAndExitGame();
        }
    }

    private void addItemsToCanvas(Level level) {
        gameView.addToCanvas(level.getWall().getToken(), 10, 0, Color.CYAN);
        gameView.addColorStringToCanvas(Snake.head.toString(), Snake.head.getY(), Snake.head.getX());
        gameView.addToCanvas(apple.toString(), apple.getY(), apple.getX(), Color.YELLOW);
        gameView.addToCanvas("Player: " + player.getName(), 1, 1);
        gameView.addToCanvas(" Level: " + level.getNumber(), 2, 1, Color.ORANGE);
        gameView.addToCanvas("Highscore: " + String.format(SCORE_FORMAT, player.getHighscore()), 1, COLUMNS - 17);
        gameView.addToCanvas("Score: " + String.format(SCORE_FORMAT, player.getCurrentscore()), 2, COLUMNS - 13, Color.ORANGE);
        gameView.addToCanvas("Target:   " + String.format(APPLE_COUNTER_FORMAT, level.getCountApple()), 4, COLUMNS - 14);
        gameView.addToCanvas("Current:   " + String.format(APPLE_COUNTER_FORMAT, level.getCountAppleEaten()), 5, COLUMNS - 15, Color.RED);
        gameView.addToCanvas("Bonus: " + String.format(SCORE_FORMAT, level.getBonus()), 8, COLUMNS - 26, Color.GREEN);
        if (snake.tails.size() >= 1) {
            for (Tail tail : snake.tails) {
                gameView.addColorStringToCanvas(tail.toString(), tail.getY(), tail.getX());
            }
        }
    }

    private void lostAndExitGame() {
        play = false;
        playLoserSound();
        checkScoreAndWriteToFile();
        showLoserScreen();
        initGame();
        play();
    }

    /**
     * Returns the file name of the highscore file.
     * @return static HIGHSCORE_FILE variable.
     */
    public static String getHighscoreFile() { return HIGHSCORE_FILE; }

    /**
     * Returns the rows of the gameview.
     * @return static ROWS variable.
     */
    public static int getRows() { return ROWS; }

    /**
     * Returns the columns of the gameview.
     * @return static COLUMNS variable.
     */
    public static int getColumns() { return COLUMNS; }
}