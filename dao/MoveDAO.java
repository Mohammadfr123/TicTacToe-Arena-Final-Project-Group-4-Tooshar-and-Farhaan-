package TicTacToe.dao;

import TicTacToe.db.Database;
import TicTacToe.model.Move;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {
    private final Database database;

    public MoveDAO(Database database) {
        this.database = database;
    }

    public Move create(long matchId, long playerId,
                       int boardPosition, int moveNumber) throws SQLException {
        String sql = "INSERT INTO moves(match_id, player_id, " +
                "board_position, move_number) VALUES (?, ?, ?, ?)";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, matchId);
            stmt.setLong(2, playerId);
            stmt.setInt(3, boardPosition);
            stmt.setInt(4, moveNumber);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException(
                            "The new move did not receive an id.");
                }
                return new Move(keys.getLong(1), matchId, playerId,
                        boardPosition, moveNumber);
            }
        }
    }

    public List<Move> findByMatch(long matchId) throws SQLException {
        String sql = "SELECT id, match_id, player_id, " +
                "board_position, move_number" +
                " FROM moves WHERE match_id = ?" +
                " ORDER BY move_number";

        List<Move> moves = new ArrayList<>();

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(new Move(
                            rs.getLong("id"),
                            rs.getLong("match_id"),
                            rs.getLong("player_id"),
                            rs.getInt("board_position"),
                            rs.getInt("move_number")));
                }
            }
        }
        return moves;
    }
}

