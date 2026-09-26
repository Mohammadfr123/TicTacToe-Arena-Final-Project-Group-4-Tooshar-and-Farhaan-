package TicTacToe.model;

import java.time.LocalDateTime;

/**
 * Represents a single Match between two players.
 */
public class Match {
    private int id;
    private int player1Id;
    private int player2Id;
    private Integer winnerId; // Use Integer so it can be null (draw)
    private LocalDateTime playedAt;

    public Match(int id, int player1Id, int player2Id, Integer winnerId, LocalDateTime playedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.playedAt = playedAt;
    }

    public int getId() { return id; }
    public int getPlayer1Id() { return player1Id; }
    public int getPlayer2Id() { return player2Id; }
    public Integer getWinnerId() { return winnerId; }
    public LocalDateTime getPlayedAt() { return playedAt; }
}
