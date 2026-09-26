package TicTacToe.model;

import java.time.LocalDateTime;

/**
 * Represents a Tournament.
 */
public class Tournament {
    private int id;
    private String name;
    private LocalDateTime createdAt;

    public Tournament(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return name;
    }
}
