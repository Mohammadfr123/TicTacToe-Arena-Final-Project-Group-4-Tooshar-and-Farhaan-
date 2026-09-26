package TicTacToe.model;

/**
 * Represents a Player in the system.
 */
public class Player {
    private int id;
    private String username;

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return username; // This is what shows up in the JavaFX ListView
    }
}
