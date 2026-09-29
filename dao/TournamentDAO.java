package TicTacToe.dao;

import TicTacToe.db.Database;
import TicTacToe.model.Tournament;
import TicTacToe.model.TournamentStanding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAO {
    private final Database database;

    public TournamentDAO(Database database) {
        this.database = database;
    }


    public Tournament create(String name) throws SQLException {
        String sql = "INSERT INTO tournaments(name, created_at) VALUES (?, ?)";
        Instant now = Instant.now();

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, now.toString());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException(
                            "The new tournament did not receive an id.");
                }
                return new Tournament(keys.getLong(1), name, now);
            }
        }
    }

    public List<Tournament> findAll() throws SQLException {
        String sql = "SELECT id, name, created_at FROM tournaments ORDER BY created_at DESC";
        List<Tournament> tournaments = new ArrayList<>();

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tournaments.add(mapRow(rs));
            }
        }
        return tournaments;
    }

    public Tournament findById(long id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM tournaments WHERE id = ?";

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

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM tournaments WHERE id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean updateName(long id, String name) throws SQLException {
        String sql = "UPDATE tournaments SET name = ? WHERE id = ?";

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setLong(2, id);

            return stmt.executeUpdate() == 1;
        }
    }

    public List<TournamentStanding> standings(long tournamentId) throws SQLException {
        String sql =
                "SELECT p.username," +
                        "       COUNT(m.id) AS played," +
                        "       SUM(CASE WHEN m.winner_id = p.id THEN 1 ELSE 0 END) AS wins," +
                        "       SUM(CASE WHEN m.winner_id IS NULL THEN 1 ELSE 0 END) AS draws," +
                        "       SUM(CASE WHEN m.winner_id = p.id THEN 3 " +
                        "                WHEN m.winner_id IS NULL THEN 1 ELSE 0 END) AS points " +
                        "FROM players p " +
                        "JOIN matches m ON p.id = m.player1_id OR p.id = m.player2_id " +
                        "JOIN rounds r ON r.id = m.round_id " +
                        "WHERE r.tournament_id = ? " +
                        "GROUP BY p.id, p.username " +
                        "ORDER BY points DESC, wins DESC, played ASC, p.username ASC";

        List<TournamentStanding> lstStandings = new ArrayList<>();

        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tournamentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lstStandings.add(new TournamentStanding(
                            rs.getString("username"),
                            rs.getLong("played"),
                            rs.getLong("wins"),
                            rs.getLong("draws"),
                            rs.getLong("points")));
                }
            }
        }
        return lstStandings;
    }

    private Tournament mapRow(ResultSet rs) throws SQLException {
        return new Tournament(
                rs.getLong("id"),
                rs.getString("name"),
                Instant.parse(rs.getString("created_at")));
    }

}

