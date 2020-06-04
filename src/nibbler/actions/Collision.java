package nibbler.actions;

import nibbler.game.Apple;
import nibbler.game.Snake;
import nibbler.game.Tail;

public class Collision {
    public boolean collideWithApple(Snake snake, Apple apple) {
        if (snake.head.getX() == apple.getX() && snake.head.getY() == apple.getY()) {
            return true;
        }
        return false;
    }

    public boolean collideWithSelf(Snake snake) {
        for (int i = 0; i < snake.tails.size(); i++) {
            Tail tail = snake.tails.get(i);
            if (snake.head.getX() == tail.getX() && snake.head.getY() == tail.getY() && !tail.isWait()) {
                return true;
            }
        }
        return false;
    }
}
