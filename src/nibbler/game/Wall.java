package nibbler.game;

public class Wall {
    private String token;

    /**
     * Creates a new Wall Object with a String layout.
     * @param token layout of the wall.
     */
    public Wall(String token) {
        this.token = token;
    }

    /**
     * Returns the String layout of the wall.
     * @return token variable.
     */
    public String getToken() {
        return token;
    }
}
