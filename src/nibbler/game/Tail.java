package nibbler.game;

public class Tail extends SnakePart {

    boolean wait = true;

    public Tail(int y, int x) {
        super(y, x);
        this.color = "G";
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }
}
