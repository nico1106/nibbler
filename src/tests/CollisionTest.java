package tests;

import nibbler.game.Apple;
import nibbler.actions.Collision;
import nibbler.game.Snake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionTest {

    Apple apple;
    Snake snake;

    Collision collision;

    @BeforeEach
    void setUp() {
        snake = new Snake();
        apple = new Apple();
        collision = new Collision();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void collideWithApple() {
        int x,y;
        x = apple.getX(); y = apple.getY();
        snake.head.setPosition(y, x);
        assertTrue(collision.collideWithApple(snake, apple));
    }

    @Test
    void collideWithSelf() {
        for (int i = 0; i<=15; i++) {
            snake.move();
            snake.addTail();
        }

        Random random = new Random();
        int r = random.nextInt(snake.tails.size());

        snake.head.setPosition(snake.tails.get(r).getY(), snake.tails.get(r).getX());
        assertTrue(collision.collideWithSelf(snake));

    }
}