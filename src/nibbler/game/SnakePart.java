package nibbler.game;

public abstract class SnakePart {
    private int x, y;

    public SnakePart(int y, int x) {
        setPosition(y, x);
    }

    /**
     * Returns the current Y position of the snake part.
     * @return y variable.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the current X position of the snake part.
     * @return x variable.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the position of the snake part.
     * @param y Y position.
     * @param x X position.
     */
    public void setPosition(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * Returns the character for the snake part that should be displayed on the canvas.
     * @return String sign for snake part. */
    @Override
    public abstract String toString();
}
