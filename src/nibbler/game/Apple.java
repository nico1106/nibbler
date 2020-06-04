package nibbler.game;

import java.util.Random;


public class Apple {
    Random random;
    private char sign = '*';
    private int x, y;

    public Apple() {
        x = -1;
        y = -1;
    }

    public int getX() {
        return x;
    }

    public void reset() {
        x = -1;
        y = -1;
    }


    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    @Override
    public String toString(){
        return Character.toString(sign);
    }



}
