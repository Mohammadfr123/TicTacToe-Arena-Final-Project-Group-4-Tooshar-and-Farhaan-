package TicTacToe.dao;

import TicTacToe.model.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PlayerDAO handles all database operations for Players.
 */
public class PlayerDAO {
    public Player create(String username) {
        String sql = "INSERT INTO players(username) VALUES(?)";
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return new Player(rs.getInt(1), username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players";
        try (Connection conn = DatabaseManager.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(new Player(rs.getInt("id"), rs.getString("username")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public void update(Player player) {
        String sql = "UPDATE players SET username = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player.getUsername());
            pstmt.setInt(2, player.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
