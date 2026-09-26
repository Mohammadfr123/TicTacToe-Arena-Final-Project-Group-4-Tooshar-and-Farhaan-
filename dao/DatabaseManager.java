package TicTacToe.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles the connection and ensures the tables exist.
 * This is the "Schema-on-first-run" logic.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tictactoe.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String playersTable = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL);";
        
        String tournamentsTable = "CREATE TABLE IF NOT EXISTS tournaments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";
        
        String matchesTable = "CREATE TABLE IF NOT EXISTS matches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player1_id INTEGER, " +
                "player2_id INTEGER, " +
                "winner_id INTEGER, " +
                "played_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(player1_id) REFERENCES players(id), " +
                "FOREIGN KEY(player2_id) REFERENCES players(id));";

        String movesTable = "CREATE TABLE IF NOT EXISTS moves (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id INTEGER, " +
                "player_id INTEGER, " +
                "board_position INTEGER, " +
                "move_number INTEGER, " +
                "FOREIGN KEY(match_id) REFERENCES matches(id), " +
                "FOREIGN KEY(player_id) REFERENCES players(id));";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(playersTable);
            stmt.execute(tournamentsTable);
            stmt.execute(matchesTable);
            stmt.execute(movesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
