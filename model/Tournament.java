package TicTacToe.model;

import java.time.Instant;

public class Tournament {
    private long id;
    private String name;
    private Instant createdAt;

    // For the UI to create a new tournament (id is 0 until saved)
    public Tournament(String name) {
        this.name = name;
        this.createdAt = Instant.now();
        this.id = 0;
    }

    //overloading, for the DAO to reconstruct from a database row
    public Tournament(long id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // For renaming option
    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tournament)) return false;
        Tournament other = (Tournament) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
