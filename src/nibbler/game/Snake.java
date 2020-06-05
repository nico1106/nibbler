package nibbler.game;

import nibbler.Nibbler;

import java.util.ArrayList;

public class Snake implements Movable {
    public static Head head;
    public ArrayList<Tail> tails;

    private boolean waitToMove = false;

    /** Creates a new Snake Object with a default tails size of 3. */
    public Snake() {
        head = new Head(Nibbler.getColumns() / 2, Nibbler.getColumns() / 2 - 4);
        tails = new ArrayList<Tail>();
        for (int i = 1; i <= 3; i++) { addTail(); }
    }

    private void moveTails(){
        if (tails.size() >= 1) {
            for (int i = tails.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    if (tails.get(i).isWait()) {
                        tails.get(i).setWait(false);
                    } else {
                        tails.get(i).setPosition(head.getY(), head.getX());
                    }
                } else {
                    if (tails.get(i).isWait()) {
                        tails.get(i).setWait(false);
                    } else {
                        tails.get(i).setPosition(tails.get(i - 1).getY(), tails.get(i - 1).getX());
                    }
                }
            }
        }
    }

    private void moveHead(){
        switch (head.getDirection()) {
            case UP:
                head.setPosition(head.getY() - 1, head.getX());
                break;
            case RIGHT:
                head.setPosition(head.getY(), head.getX() + 1);
                break;
            case DOWN:
                head.setPosition(head.getY() + 1, head.getX());
                break;
            case LEFT:
                head.setPosition(head.getY(), head.getX() - 1);
                break;
        }
    }

    /** Moves the snake's head and tails based on the current direction. */
    public void move() {
        moveTails();
        moveHead();
    }

    /** Add a new tail to the snake at the end of the tails list. */
    public void addTail() {
        if (tails.size() < 1) {
            int y = head.getY();
            int x = head.getX();
            tails.add(new Tail(y, x));
        } else {
            int y = tails.get(tails.size() - 1).getY();
            int x = tails.get(tails.size() - 1).getX();
            tails.add(new Tail(y, x));
        }
    }

    /**
     * Specifies, if the snake should be moved.
     * @return TRUE, if the snake shouldn't be moved, otherwise FALSE.
     */
    public boolean getWaitToMove(){ return waitToMove; }

    /**
     * Updates the waitToMove variable of the snake.
     * @param waitToMove TRUE, if the snake shouldn't be moved, otherwise FALSE.
     */
    public void setWaitToMove(boolean waitToMove) { this.waitToMove = waitToMove; }
}
