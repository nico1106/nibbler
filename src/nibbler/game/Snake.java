package nibbler.game;

import nibbler.Nibbler;

import java.util.ArrayList;

public class Snake {
    public static Head head;
    public ArrayList<Tail> tails;
    public boolean waitToMove = false;

    public Snake() {
        head = new Head(Nibbler.getColumns() / 2, Nibbler.getColumns() / 2 - 4 );
        tails = new ArrayList<Tail>();
        // add 3 tails to the snake
        for(int i=1;i<=3;i++){
            addTail();
        }
    }

    public void addTail() {
        // add tail
        if(tails.size() < 1) {
            int y = head.getY();
            int x = head.getX();
            tails.add(new Tail(y, x));
        } else {
            int y = tails.get(tails.size()-1).getY();
            int x = tails.get(tails.size()-1).getX();
            tails.add(new Tail(y, x));
        }
    }

    public void move() {
        // move tails
        if (tails.size() >= 1) {
            for(int i=tails.size()-1; i>=0;i--) {
                if (i==0) {
                    if (tails.get(i).isWait()) {
                        tails.get(i).setWait(false);
                    } else {
                        tails.get(i).setPosition(head.getY(), head.getX());
                    }
                } else {
                   if (tails.get(i).isWait()) {
                        tails.get(i).setWait(false);
                    } else {
                        tails.get(i).setPosition(tails.get(i-1).getY(), tails.get(i-1).getX());
                    }
                }

            }
        }

        // move head
        switch (head.getDirection()){
            case UP:
                head.setPosition(head.getY()-1, head.getX());
                break;
            case RIGHT:
                head.setPosition(head.getY(), head.getX()+1);
                break;
            case DOWN:
                head.setPosition(head.getY()+1, head.getX());
                break;
            case LEFT:
                head.setPosition(head.getY(), head.getX()-1);
                break;
        }


    }
}
