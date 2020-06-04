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
        assertTrue(snake.head.getX() == Nibbler.getColumns() / 2);
    }

    @org.junit.jupiter.api.Test
    void getYPosition() {
        assertTrue(snake.head.getY() == Nibbler.getColumns() / 2 - 4 );
    }

    @org.junit.jupiter.api.Test
    void getHead() {
        assertTrue(snake.head.toString().equals("R"));
        assertTrue(snake.head instanceof Head && snake.head instanceof SnakePart );
    }

    @org.junit.jupiter.api.Test
    void addTail() {
        assertTrue(snake.tails.size() == 0);
        snake.addTail();
        assertTrue(snake.tails.size() == 1);
        assertTrue(snake.tails.get(0) instanceof Tail && snake.tails.get(0) instanceof SnakePart);
    }

    @Test
    void move() {
        int x, y;
        x = snake.head.getX(); y = snake.head.getY();
        snake.head.setDirection(Head.Direction.RIGHT);
        snake.move();
        assertTrue(x+1 == snake.head.getX());
        assertTrue(y == snake.head.getY());

        x = snake.head.getX(); y = snake.head.getY();
        snake.head.setDirection(Head.Direction.UP);
        snake.move();
        assertTrue(x == snake.head.getX());
        assertTrue(y-1 == snake.head.getY());

        x = snake.head.getX(); y = snake.head.getY();
        snake.head.setDirection(Head.Direction.LEFT);
        snake.move();
        assertTrue(x-1 == snake.head.getX());
        assertTrue(y == snake.head.getY());

        x = snake.head.getX(); y = snake.head.getY();
        snake.head.setDirection(Head.Direction.DOWN);
        snake.move();
        assertTrue(x == snake.head.getX());
        assertTrue(y+1 == snake.head.getY());

    }
}