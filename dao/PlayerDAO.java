package TicTacToe.dao;

import TicTacToe.db.Database;
import TicTacToe.model.Player;
import TicTacToe.model.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private final Database database;

    public PlayerDAO(Database database) {
        this.database = database;
    }

    public Player create(String username) throws SQLException {
        String sql = "INSERT INTO players(username, created_at) VALUES (?, ?)";
        Instant now = Instant.now(); //storing time

        //opening connection
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username); //passing username to placeholder1
            stmt.setString(2, now.toString()); //passing time as string to placeholder2
            stmt.executeUpdate(); //executing the command

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("The new player did not receive an id.");
                }
                return new Player(keys.getLong(1), username, now);
            }
        }
    }

    public List<Player> findAll() throws SQLException {
        String sql = "SELECT id, username, created_at FROM players ORDER BY username";
        List<Player> players = new ArrayList<>();

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                players.add(mapRow(rs));
            }
        }
        return players;
    }

    public Player findById(long id) throws SQLException {
        String sql = "SELECT id, username, created_at FROM players WHERE id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public boolean update(Player player) throws SQLException {
        String sql = "UPDATE players SET username = ? WHERE id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player.getUsername());
            stmt.setLong(2, player.getId());

            return stmt.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM players WHERE id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    public PlayerStats stats(long playerId) throws SQLException {
        String sql =
                "SELECT COUNT(m.id) AS played," +
                        "       COALESCE(SUM(CASE WHEN m.winner_id = ? THEN 1 ELSE 0 END), 0) AS wins, " + //Coalesce returns the first non-NULL value in a list
                        "       COALESCE(SUM(CASE WHEN m.winner_id IS NULL THEN 1 ELSE 0 END), 0) AS draws " +
                        "FROM matches m " +
                        "WHERE m.player1_id = ? OR m.player2_id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, playerId);
            stmt.setLong(2, playerId);
            stmt.setLong(3, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return new PlayerStats(
                        rs.getLong("played"),
                        rs.getLong("wins"),
                        rs.getLong("draws"));
            }
        }
    }

    private Player mapRow(ResultSet rs) throws SQLException {
        return new Player(
                rs.getLong("id"),
                rs.getString("username"),
                Instant.parse(rs.getString("created_at")));
    }

}

