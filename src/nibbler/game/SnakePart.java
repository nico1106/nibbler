package nibbler.game;

public abstract class SnakePart {
    protected String color;
    protected int x, y;

    public SnakePart(int y, int x) {
        this.y = y;
        this.x = x;
    }

    protected String getColor() { return color; }
    public int getY() { return y; }
    public void setPosition(int y, int x) { this.y = y; this.x = x; }
    public int getX() { return x; }

    @Override
    public String toString() {
        return color;
    }
}
