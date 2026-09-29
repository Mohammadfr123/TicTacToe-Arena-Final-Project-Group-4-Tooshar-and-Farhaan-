package TicTacToe.dao;

import TicTacToe.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoundDAO {
    private final Database database;

    public RoundDAO(Database database) {
        this.database = database;
    }

    public long createNext(long tournamentId) throws SQLException {
        String lookupSql = "SELECT COALESCE(MAX(round_number), 0) + 1 FROM rounds WHERE tournament_id = ?";
        String insertSql = "INSERT INTO rounds(tournament_id, round_number) VALUES (?, ?)";

        try (Connection conn = database.open()) {
            conn.setAutoCommit(false); //safety purposes

            try {
                // 1. Find the next round number
                int roundNumber;
                try (PreparedStatement lookup = conn.prepareStatement(lookupSql)) {
                    lookup.setLong(1, tournamentId);
                    try (ResultSet rs = lookup.executeQuery()) {
                        rs.next();
                        roundNumber = rs.getInt(1);
                    }
                }

                // 2. Insert the new round
                long roundId;
                try (PreparedStatement insert = conn.prepareStatement(insertSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    insert.setLong(1, tournamentId);
                    insert.setInt(2, roundNumber);
                    insert.executeUpdate();

                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The new round did not receive an id.");
                        }
                        roundId = keys.getLong(1);
                    }
                }

                // 3. Commit the transaction
                conn.commit();
                return roundId;

            } catch (SQLException e) {
                conn.rollback(); //undo changes in case of errors
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}

