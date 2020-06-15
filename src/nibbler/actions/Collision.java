package nibbler.actions;

import nibbler.game.Apple;
import nibbler.game.Snake;
import nibbler.game.Tail;

import java.util.ArrayList;

public class Collision {
    /**
     * Indicates if the snake collides with the apple.
     * @param snake snake object.
     * @param apples list.
     * @return TRUE, if the snake collides with the apples, otherwise FALSE.
     */
    public boolean collideWithApple(Snake snake, ArrayList<Apple> apples) {
        for(int i=0;i<apples.size();i++) {
            Apple apple = apples.get(i);
            if (Snake.head.getX() == apple.getX() && Snake.head.getY() == apple.getY()) {
                apples.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates it the snake collides with itself.
     * @param snake snake object.
     * @return TRUE, if the snake collides with itself, otherwise FALSE.
     */
    public boolean collideWithSelf(Snake snake) {
        for (int i = 0; i < snake.tails.size(); i++) {
            Tail tail = snake.tails.get(i);
            if (Snake.head.getX() == tail.getX() && Snake.head.getY() == tail.getY() && !tail.isWait()) return true;
        }
        return false;
    }
}
