package nibbler.actions;

import nibbler.game.Apple;
import nibbler.game.Snake;
import nibbler.game.Tail;

public class Collision {
    /**
     * Indicates if the snake collides with the apple.
     * @param snake snake object.
     * @param apple apple object.
     * @return TRUE, if the snake collides with the apple, otherwise FALSE.
     */
    public boolean collideWithApple(Snake snake, Apple apple) {
        return Snake.head.getX() == apple.getX() && Snake.head.getY() == apple.getY();
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
