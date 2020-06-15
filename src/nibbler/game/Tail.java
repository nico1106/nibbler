package nibbler.game;

public class Tail extends SnakePart implements Cloneable, Drawable {

    private boolean wait = true;

    /**
     * Creates a new tail for the snake.
     * @param y Y position.
     * @param x X position.
     */
    public Tail(int y, int x) {
        super(y, x);
    }

    /**
     * Initially the tail has the same X/Y position of his predecessor.
     * Therefor the tail need to wait 1 move of the snake before moving the tail.
     * @return TRUE, if the tail is in WAITING state, otherwise FALSE:
     */
    public boolean isWait() {
        return wait;
    }

    /**
     * Sets the WAITING state of the tail to either TRUE or FALSE.
     * @param wait TRUE, if the tail should wait, otherwise FALSE.
     */
    public void setWait(boolean wait) {
        this.wait = wait;
    }

    /** {@inheritDoc} */
    public String toString(){ return "G"; }

    /**
     * Clones the tail with its values.
     * @return Tail object.
     */
    @Override
    public Tail clone() {
        Tail copy = new Tail(getY(), getX());
        return copy;
    }
}
