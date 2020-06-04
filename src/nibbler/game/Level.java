package nibbler.game;

import java.util.ArrayList;

public class Level {

    private int number;
    private int tact;
    private int countApple;
    private int bonus;
    private int countAppleEaten = 0;
    private ArrayList<String> wall_level = new ArrayList<>();

    private Wall wall;

    public Level(int number, int countApple, int tact, String token, int bonus) {
        this.number = number;
        this.tact = tact;
        this.countApple = countApple;
        this.bonus = bonus;
        this.wall = new Wall(token);
    }

    public int getNumber() {
        return number;
    }

    public Wall getWall() {
        return wall;
    }

    public int getCountApple(){
        return countApple;
    }

    public int getCountAppleEaten(){
        return countAppleEaten;
    }

    public void setCountAppleEaten(int count){
        this.countAppleEaten = count;
    }

    public int getTact(){
        return tact;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus){
        this.bonus = bonus;
    }
}
