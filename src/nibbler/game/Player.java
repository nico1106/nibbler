package nibbler.game;

import nibbler.actions.FileHandler;

public class Player {
    private String name;
    private int highscore;
    private int currentscore;

    /** Creates a new Player object with a default name and a highscore is loaded which is stored locally in a file. */
    public Player() {
        name = "Noname";
        currentscore = 0;
        highscore = FileHandler.getHighscoreFromFile(this);
    }

    /**
     * Returns the name of the player.
     * @return name variable.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the player object.
     * @param name new name of the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the highscore of the player.
     * @return highscore variable.
     */
    public int getHighscore() {
        return highscore;
    }

    /**
     * Sets a new highscore for the player.
     * @param highscore new highscore of the player.
     */
    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    /**
     * Returns the current score of the player.
     * @return currentscore variable.
     */
    public int getCurrentscore() {
        return currentscore;
    }

    /**
     * Sets a new current score for the player.
     * @param currentscore new current score of the player.
     */
    public void setCurrentscore(int currentscore) {
        this.currentscore = currentscore;
    }
}
