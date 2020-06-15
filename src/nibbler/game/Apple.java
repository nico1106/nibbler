package nibbler.game;

public class Apple implements Drawable {
    private int x, y;

    /** Creates a new Apple Object.
     * X and Y Coordinates are reset to -1.
     */
    public Apple() {
        reset();
    }

    /**
     * The X position of the apple is returned.
     * @return Apple's X coordinate. */
    public int getX() {
        return x;
    }

    /** Resets the X and Y position of the apple to X = -1 and Y = -1. */
    public void reset() {
        setX(-1);
        setY(-1);
    }

    /**
     * Set a new X coordinate for the apple.
     * @param x this will be the new X coordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * The Y position of the apple is returned.
     * @return Apple's Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Set a new Y coordinate for the apple.
     * @param y this will be the new Y coordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * The sign for the Apple that should be displayed on the canvas will be returned.
     * @return String sign for the apple.
     */
    @Override
    public String toString() {
        return "*";
    }
}
