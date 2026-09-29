package TicTacToe.dao;

import TicTacToe.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class MatchDAO {
    private final Database database;

    public MatchDAO(Database database) {
        this.database = database;
    }

    public long create(Long roundId, long player1Id, long player2Id, Long winnerId) throws SQLException {
        //SQL querry
        String sql = "INSERT INTO matches(round_id, player1_id, player2_id, " +
                "winner_id, played_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            // round_id: nullable (null = casual match, no tournament)
            if (roundId == null) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setLong(1, roundId);
            }

            // player1_id: always present
            stmt.setLong(2, player1Id);

            // player2_id: always present
            stmt.setLong(3, player2Id);

            // winner_id: nullable (null = draw)
            if (winnerId == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setLong(4, winnerId);
            }

            // played_at: current instant
            stmt.setString(5, Instant.now().toString());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("The new match did not receive an id.");
                }
                return keys.getLong(1);
            }
        }
    }
}