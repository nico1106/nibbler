package nibbler.game;

public class Head extends SnakePart {

    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    private Direction direction;

    public Head(int y, int x) {
        super(x, y);
        this.color = "R";
        this.direction = Direction.RIGHT;
    }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
