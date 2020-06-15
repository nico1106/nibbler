package tests;

import nibbler.actions.Collision;
import nibbler.game.Apple;
import nibbler.game.Snake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionTest {

    Apple apple;
    Snake snake;
    ArrayList<Apple> apples = new ArrayList<>();

    Collision collision;

    @BeforeEach
    void setUp() {
        snake = new Snake();
        apple = new Apple();
        apples.add(apple);
        collision = new Collision();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void collideWithApple() {
        int x, y;
        x = apple.getX();
        y = apple.getY();
        Snake.head.setPosition(y, x);
        assertTrue(collision.collideWithApple(snake, apples));
    }

    @Test
    void collideWithSelf() {
        for (int i = 0; i <= 15; i++) {
            snake.move();
            snake.addTail();
        }

        Random random = new Random();
        int r = random.nextInt(snake.tails.size());

        Snake.head.setPosition(snake.tails.get(r).getY(), snake.tails.get(r).getX());
        assertTrue(collision.collideWithSelf(snake));

    }
}