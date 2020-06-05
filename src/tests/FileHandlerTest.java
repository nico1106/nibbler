package tests;

import nibbler.Nibbler;
import nibbler.actions.FileHandler;
import nibbler.game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    Player player;
    Player player2;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setName("test");
        player2 = new Player();
        player.setName("test_2");
    }

    @AfterEach
    void tearDown() {
        // delete the nibbler_highscore.txt after each test and start from scratch.
        File highscorefile = new File(Nibbler.getHighscoreFile());
        if (highscorefile.exists()) {
            highscorefile.delete();
        }
    }

    @Test
    void getHighscoreFromFile() {
        int highscore = FileHandler.getHighscoreFromFile(player);
        assertTrue(highscore == 0);
        int player2_highscore = 10;
        player2.setCurrentscore(player2_highscore);
        FileHandler.writeScoreToFile(player2);
        assertTrue(player2_highscore == FileHandler.getHighscoreFromFile(player2));
    }

    @Test
    void writeScoreToFile() {
        int highscore = player.getHighscore();
        int new_score = highscore + 1;
        player.setCurrentscore(new_score);
        if (player.getCurrentscore() > player.getHighscore()) {
            FileHandler.writeScoreToFile(player);
            assertTrue(new_score == FileHandler.getHighscoreFromFile(player));
            player.setCurrentscore(new_score + 1);
            FileHandler.writeScoreToFile(player);
            assertTrue(new_score + 1 == FileHandler.getHighscoreFromFile(player));
        } else {
            fail("Test was not successful. Your function is not correctly implemented.");
        }

    }
}