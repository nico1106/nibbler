package nibbler.actions;

import nibbler.Nibbler;
import nibbler.game.Player;

import java.io.*;
import java.util.Scanner;

public class FileHandler {

    public static int getHighscoreFromFile(Player player){
        int highscore = 0;
        try {
            File highscorefile = new File(Nibbler.getHighscoreFile());
            Scanner myReader = new Scanner(highscorefile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] result = data.split(";");
                if (result[0].equals(player.getName())) {
                    highscore = Integer.parseInt(result[1]);
                    break;
                }
            }
            myReader.close();
            return highscore;
        } catch (FileNotFoundException e) {
            return 0;
        }
    }

    public static void writeScoreToFile(Player player){
        StringBuilder sb = new StringBuilder();
        String oldContent = "";
        try {
            File highscorefile = new File(Nibbler.getHighscoreFile());
            if (!highscorefile.exists()) {
                highscorefile.createNewFile();
            }
            sb.append(player.getName()); sb.append(";"); sb.append(player.getCurrentscore());
            if(getHighscoreFromFile(player)>0){
                BufferedReader reader = new BufferedReader(new FileReader(Nibbler.getHighscoreFile()));
                String line = reader.readLine();
                while(line != null) {
                    oldContent = oldContent + line + System.lineSeparator();
                    line = reader.readLine();
                }
                String newContent = oldContent.replace(player.getName()+";"+getHighscoreFromFile(player), sb.toString());
                FileWriter myWriter = new FileWriter(highscorefile.getAbsoluteFile());
                myWriter.write(newContent);
                myWriter.close();
            } else {
                if(player.getCurrentscore()>0){
                    FileWriter myWriter = new FileWriter(highscorefile.getAbsoluteFile(),true);
                    myWriter.write(sb.toString()+"\n");
                    myWriter.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
