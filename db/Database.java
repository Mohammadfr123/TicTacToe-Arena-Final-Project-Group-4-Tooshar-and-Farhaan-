package TicTacToe.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

 //Owns the SQLite database file location, provides connections, and creates the schema on first run.
 //Every DAO takes a Database in its constructor and calls database.open() to get a connection.
public class Database {

    private final Path databasePath;

    public Database(Path databasePath) {
        this.databasePath = databasePath.toAbsolutePath().normalize();
    }

    public static Database configured() {
        String override = System.getProperty("tictactoe.db");
        Path path;
        if (override == null || override.isBlank()) {
            path = Path.of(System.getProperty("user.home"), ".tictactoe-arena", "arena.db");
        } else {
            path = Path.of(override);
        }
        return new Database(path);
    }

    //Returns a new Connection to the SQLite database
    public Connection open() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    //Creates the parent directory if needed and runs the CREATE TABLE IF NOT EXISTS DDL for all five tables
    public void initialize() throws SQLException, IOException {
        Path parent = databasePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (Connection conn = open();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS players (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    username TEXT NOT NULL COLLATE NOCASE UNIQUE," +
                            "    created_at TEXT NOT NULL" +
                            ")");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS tournaments (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    name TEXT NOT NULL COLLATE NOCASE UNIQUE," +
                            "    created_at TEXT NOT NULL" +
                            ")");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS rounds (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    tournament_id INTEGER NOT NULL," +
                            "    round_number INTEGER NOT NULL," +
                            "    UNIQUE(tournament_id, round_number)," +
                            "    FOREIGN KEY (tournament_id) REFERENCES tournaments(id)" +
                            "        ON DELETE CASCADE" +
                            ")");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS matches (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    round_id INTEGER," +
                            "    player1_id INTEGER NOT NULL," +
                            "    player2_id INTEGER NOT NULL," +
                            "    winner_id INTEGER," +
                            "    played_at TEXT NOT NULL," +
                            "    FOREIGN KEY (round_id) REFERENCES rounds(id)" +
                            "        ON DELETE CASCADE," +
                            "    FOREIGN KEY (player1_id) REFERENCES players(id)" +
                            "        ON DELETE RESTRICT," +
                            "    FOREIGN KEY (player2_id) REFERENCES players(id)" +
                            "        ON DELETE RESTRICT," +
                            "    FOREIGN KEY (winner_id) REFERENCES players(id)" +
                            "        ON DELETE RESTRICT" +
                            ")");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS moves (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    match_id INTEGER NOT NULL," +
                            "    player_id INTEGER NOT NULL," +
                            "    board_position INTEGER NOT NULL" +
                            "        CHECK(board_position BETWEEN 0 AND 8)," +
                            "    move_number INTEGER NOT NULL" +
                            "        CHECK(move_number BETWEEN 1 AND 9)," +
                            "    UNIQUE(match_id, move_number)," +
                            "    FOREIGN KEY (match_id) REFERENCES matches(id)" +
                            "        ON DELETE CASCADE," +
                            "    FOREIGN KEY (player_id) REFERENCES players(id)" +
                            "        ON DELETE RESTRICT" +
                            ")");
        }
    }
}

