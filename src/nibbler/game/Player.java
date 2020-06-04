package nibbler.game;

import nibbler.actions.FileHandler;

public class Player {
    private String name;
    private int highscore;
    private int currentscore;

    public Player() {
        name = "Noname";
        currentscore = 0;
        highscore = FileHandler.getHighscoreFromFile(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int getCurrentscore() {
        return currentscore;
    }

    public void setCurrentscore(int currentscore) {
        this.currentscore = currentscore;
    }
}
