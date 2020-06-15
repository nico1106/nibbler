package nibbler.game;

import java.util.ArrayList;

public class Level {

    private int number;
    private int tact;
    private int countApple;
    private int bonus;
    private int countAppleEaten = 0;
    public ArrayList<Apple> apples = new ArrayList<>();

    private Wall wall;

    /**
     * Creates a new Level Object.
     * @param number level number.
     * @param countApple specifies how many apples have to be eaten by the snake to get to the next level.
     * @param tact speed of the snake.
     * @param token String layout of the wall.
     * @param bonus specifies the amount of bonus for a level the player can get.
     */
    public Level(int number, int countApple, int tact, String token, int bonus) {
        this.number = number;
        this.tact = tact;
        this.countApple = countApple;
        this.bonus = bonus;
        this.wall = new Wall(token);
        for(int i=1;i<=countApple;i++) {
            Apple apple = new Apple();
            apples.add(apple);
        }
    }

    /**
     * The current Level Number will be returned.
     * @return number variable.
     */
    public int getNumber() {
        return number;
    }

    /**
     * The current Wall Object will be returned.
     * @return wall object of the level.
     */
    public Wall getWall() {
        return wall;
    }

    /**
     * Number of apples that need to be eaten will be returned.
     * @return countApple variable.
     */
    public int getCountApple() {
        return countApple;
    }

    /**
     * Returns the number of apples that have already been eaten by the snake.
     * @return countAppleEaten variable. */
    public int getCountAppleEaten() {
        return countAppleEaten;
    }

    /**
     * Sets the number of apples that have been eaten.
     * @param count Number of apples that have been eaten.
     */
    public void setCountAppleEaten(int count) {
        this.countAppleEaten = count;
    }

    /**
     * Returns the current tact of the level.
     * @return tact variable.
     */
    public int getTact() {
        return tact;
    }

    /**
     * Returns the bonus number of the level.
     * @return bonus variable.
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Sets the bonus number of the level.
     * @param bonus number of bonus that should to be set.
     */
    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
