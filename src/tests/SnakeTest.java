package tests;

import nibbler.Nibbler;
import nibbler.game.Head;
import nibbler.game.Snake;
import nibbler.game.SnakePart;
import nibbler.game.Tail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeTest {

    Snake snake;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        snake = new Snake();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getXPositon() {
        assertTrue(Snake.head.getX() == Nibbler.getColumns() / 2);
    }

    @org.junit.jupiter.api.Test
    void getYPosition() {
        assertTrue(Snake.head.getY() == Nibbler.getColumns() / 2 - 4);
    }

    @org.junit.jupiter.api.Test
    void getHead() {
        assertTrue(Snake.head.toString().equals("R"));
        assertTrue(Snake.head instanceof Head && Snake.head instanceof SnakePart);
    }

    @org.junit.jupiter.api.Test
    void addTail() {
        int size = snake.tails.size();
        snake.addTail();
        assertTrue(snake.tails.size() == size + 1);
        assertTrue(snake.tails.get(0) instanceof Tail && snake.tails.get(0) instanceof SnakePart);
    }

    @Test
    void move() {
        int x, y;
        x = Snake.head.getX();
        y = Snake.head.getY();
        Snake.head.setDirection(Head.Direction.RIGHT);
        snake.move();
        assertTrue(x + 1 == Snake.head.getX());
        assertTrue(y == Snake.head.getY());

        x = Snake.head.getX();
        y = Snake.head.getY();
        Snake.head.setDirection(Head.Direction.UP);
        snake.move();
        assertTrue(x == Snake.head.getX());
        assertTrue(y - 1 == Snake.head.getY());

        x = Snake.head.getX();
        y = Snake.head.getY();
        Snake.head.setDirection(Head.Direction.LEFT);
        snake.move();
        assertTrue(x - 1 == Snake.head.getX());
        assertTrue(y == Snake.head.getY());

        x = Snake.head.getX();
        y = Snake.head.getY();
        Snake.head.setDirection(Head.Direction.DOWN);
        snake.move();
        assertTrue(x == Snake.head.getX());
        assertTrue(y + 1 == Snake.head.getY());

    }
}