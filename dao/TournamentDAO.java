package TicTacToe.dao;

import TicTacToe.model.Tournament;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * TournamentDAO handles all database operations for Tournaments.
 */
public class TournamentDAO {
    public Tournament create(String name) {
        String sql = "INSERT INTO tournaments(name) VALUES(?)";
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return new Tournament(rs.getInt(1), name, LocalDateTime.now());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tournament> findAll() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments";
        try (Connection conn = DatabaseManager.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tournaments.add(new Tournament(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    public void delete(int id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
