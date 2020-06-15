package nibbler.game;

public class Head extends SnakePart implements Drawable {

    /** ENUM class for the Direction in which the snake/head can go. */
    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    private Direction direction;

    /**
     * Creates a new Head Object with X and Y coordinates. Default direction is set to RIGHT.
     * @param y is the Y coordinate.
     * @param x is the X coordinate.
     */
    public Head(int y, int x) {
        super(x, y);
        this.direction = Direction.RIGHT;
    }

    /**
     * Returns the current head direction.
     * @return direction enum. */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set a new direction for the head.
     * @param direction specifies in which direction the head of the snake should move.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /** {@inheritDoc} */
    public String toString(){ return "R"; }
}
