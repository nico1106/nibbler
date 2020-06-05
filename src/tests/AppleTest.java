package tests;

import nibbler.game.Apple;
import nibbler.Nibbler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppleTest {

    Apple apple;

    @BeforeEach
    void setUp() {
        apple = new Apple();
        apple.setX(20);
        apple.setY(20);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getX() {
        assertTrue(apple.getX() >= 0 && apple.getX() <= Nibbler.getColumns());
    }

    @Test
    void reset() {
        int x, y;
        apple.setY(20);
        apple.setX(15);
        x = apple.getX();
        y = apple.getY();
        apple.reset();
        assertTrue(!(x == apple.getX()));
        assertTrue(!(y == apple.getY()));
        assertTrue(apple.getX() == -1 && apple.getY() == -1);
    }

    @Test
    void setX() {
        int x = 10;
        apple.setX(x);
        assertTrue(apple.getX() == x);
    }

    @Test
    void getY() {
        assertTrue(apple.getY() >= 10 && apple.getY() <= Nibbler.getRows());
    }

    @Test
    void setY() {
        int y = 10;
        apple.setY(y);
        assertTrue(apple.getY() == y);
    }

    @Test
    void testToString() {
        assertTrue(apple.toString().equals("*"));
    }
}