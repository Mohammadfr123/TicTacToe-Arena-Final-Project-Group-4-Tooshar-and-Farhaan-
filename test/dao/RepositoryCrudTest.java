package TicTacToe.dao;

import TicTacToe.db.Database;
import TicTacToe.model.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryCrudTest {

    private Database database;
    private Path tempDb;
    private PlayerDAO playerDAO;
    private TournamentDAO tournamentDAO;
    private RoundDAO roundDAO;
    private MatchDAO matchDAO;
    private MoveDAO moveDAO;

    // ---- lifecycle ----

    @BeforeEach
    void setUp() throws IOException, SQLException {
        tempDb = Files.createTempFile("test-arena-", ".db");
        database = new Database(tempDb);
        database.initialize();
        playerDAO = new PlayerDAO(database);
        tournamentDAO = new TournamentDAO(database);
        roundDAO = new RoundDAO(database);
        matchDAO = new MatchDAO(database);
        moveDAO = new MoveDAO(database);
    }

    @AfterEach
    void tearDown() {
        if (tempDb != null) {
            try { Files.deleteIfExists(tempDb); } catch (IOException suppressed) { /* clean up best effort */ }
        }
    }

    // ---- helpers ----

    private Player createPlayer(String name) throws SQLException {
        return playerDAO.create(name);
    }

    private Tournament createTournament(String name) throws SQLException {
        return tournamentDAO.create(name);
    }

    private long createRound(long tournamentId) throws SQLException {
        return roundDAO.createNext(tournamentId);
    }

    private long createMatch(Long roundId, long player1Id, long player2Id, Long winnerId) throws SQLException {
        return matchDAO.create(roundId, player1Id, player2Id, winnerId);
    }

    private Move createMove(long matchId, long playerId, int boardPosition, int moveNumber) throws SQLException {
        return moveDAO.create(matchId, playerId, boardPosition, moveNumber);
    }

    // direct queries for verification (tests may inspect the DB directly)
    private int roundCountFor(long tournamentId) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM rounds WHERE tournament_id = ?")) {
            stmt.setLong(1, tournamentId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private boolean matchExists(long matchId) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM matches WHERE id = ?")) {
            stmt.setLong(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Player playerById(long id) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, username, created_at FROM players WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs.getLong("id"), rs.getString("username"),
                            java.time.Instant.parse(rs.getString("created_at")));
                }
                return null;
            }
        }
    }

    // ===================================================================
    // PlayerDAO — CRUD + stats
    // ===================================================================

    @Test
    void create_ReturnsPlayerWithIdNameAndCreatedAt() throws SQLException {
        Player p = playerDAO.create("Alice");
        assertEquals("Alice", p.getUsername());
        assertTrue(p.getId() > 0);
        assertNotNull(p.getCreatedAt());
    }

    @Test
    void createDuplicate_ThrowsSQLException() throws SQLException {
        playerDAO.create("Alice");
        assertThrows(SQLException.class, () -> playerDAO.create("Alice"));
    }

    @Test
    void findAll_EmptyDatabase_ReturnsEmptyList() throws SQLException {
        assertTrue(playerDAO.findAll().isEmpty());
    }

    @Test
    void findAll_AfterCreates_ReturnsAllSortedByUsername() throws SQLException {
        createPlayer("Charlie");
        createPlayer("Alice");
        createPlayer("Bob");
        List<Player> all = playerDAO.findAll();
        assertEquals(3, all.size());
        assertEquals("Alice", all.get(0).getUsername());
        assertEquals("Bob", all.get(1).getUsername());
        assertEquals("Charlie", all.get(2).getUsername());
    }

    @Test
    void findById_ExistingId_ReturnsPlayer() throws SQLException {
        Player bob = createPlayer("Bob");
        Player found = playerDAO.findById(bob.getId());
        assertNotNull(found);
        assertEquals("Bob", found.getUsername());
        assertEquals(bob.getId(), found.getId());
    }

    @Test
    void findById_NonExistentId_ReturnsNull() throws SQLException {
        assertNull(playerDAO.findById(999L));
    }

    @Test
    void update_ChangesUsername() throws SQLException {
        Player p = createPlayer("OldName");
        assertTrue(playerDAO.update(new Player(p.getId(), "NewName", p.getCreatedAt())));
        Player found = playerDAO.findById(p.getId());
        assertEquals("NewName", found.getUsername());
    }

    @Test
    void update_NonExistentId_ReturnsFalse() throws SQLException {
        assertFalse(playerDAO.update(new Player(999L, "Nobody", java.time.Instant.now())));
    }

    @Test
    void delete_ExistingId_RemovesPlayer() throws SQLException {
        Player p = createPlayer("DeleteMe");
        long id = p.getId();
        assertTrue(playerDAO.delete(id));
        assertNull(playerDAO.findById(id));
    }

    @Test
    void delete_NonExistentId_ReturnsFalse() throws SQLException {
        assertFalse(playerDAO.delete(999L));
    }

    @Test
    void stats_PlayerWithNoMatches_ReturnsZeros() throws SQLException {
        Player p = createPlayer("Solo");
        PlayerStats stats = playerDAO.stats(p.getId());
        assertEquals(0L, stats.getGamesPlayed());
        assertEquals(0L, stats.getWins());
        assertEquals(0L, stats.getDraws());
    }

    @Test
    void stats_AfterWinAndDraw_ReflectsCorrectly() throws SQLException {
        Player alice = createPlayer("Alice");
        Player bob = createPlayer("Bob");

        long roundId = createRound(createTournament("T").getId());
        // Alice wins
        long matchWin = createMatch(roundId, alice.getId(), bob.getId(), alice.getId());
        createMove(matchWin, alice.getId(), 0, 1);
        createMove(matchWin, bob.getId(), 1, 2);
        // Draw
        long matchDraw = createMatch(roundId, alice.getId(), bob.getId(), null);
        createMove(matchDraw, alice.getId(), 2, 1);
        createMove(matchDraw, bob.getId(), 3, 2);

        PlayerStats a = playerDAO.stats(alice.getId());
        assertEquals(2L, a.getGamesPlayed());
        assertEquals(1L, a.getWins());
        assertEquals(1L, a.getDraws());

        PlayerStats b = playerDAO.stats(bob.getId());
        assertEquals(2L, b.getGamesPlayed());
        assertEquals(0L, b.getWins());
        assertEquals(1L, b.getDraws());
    }

    // ===================================================================
    // TournamentDAO — CRUD
    // ===================================================================

    @Test
    void create_ReturnsTournamentWithIdNameAndCreatedAt() throws SQLException {
        Tournament t = tournamentDAO.create("Summer Cup");
        assertEquals("Summer Cup", t.getName());
        assertTrue(t.getId() > 0);
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void tournamentCreateDuplicate_ThrowsSQLException() throws SQLException {
        tournamentDAO.create("Unique");
        assertThrows(SQLException.class, () -> tournamentDAO.create("Unique"));
    }

    @Test
    void tournamentFindAll_EmptyDatabase_ReturnsEmptyList() throws SQLException {
        assertTrue(tournamentDAO.findAll().isEmpty());
    }

    @Test
    void findAll_AfterCreates_ReturnsAllSortedByCreatedAtDesc() throws SQLException {
        createTournament("First");
        createTournament("Second");
        List<Tournament> all = tournamentDAO.findAll();
        assertEquals(2, all.size());
        assertEquals("Second", all.get(0).getName());
        assertEquals("First", all.get(1).getName());
    }

    @Test
    void findById_ExistingId_ReturnsTournament() throws SQLException {
        Tournament t = createTournament("MyTourney");
        Tournament found = tournamentDAO.findById(t.getId());
        assertNotNull(found);
        assertEquals("MyTourney", found.getName());
    }

    @Test
    void tournamentFindById_NonExistentId_ReturnsNull() throws SQLException {
        assertNull(tournamentDAO.findById(999L));
    }

    @Test
    void updateName_ChangesName() throws SQLException {
        Tournament t = createTournament("Old");
        assertTrue(tournamentDAO.updateName(t.getId(), "New"));
        Tournament found = tournamentDAO.findById(t.getId());
        assertEquals("New", found.getName());
    }

    @Test
    void updateName_NonExistentId_ReturnsFalse() throws SQLException {
        assertFalse(tournamentDAO.updateName(999L, "Nobody"));
    }

    @Test
    void delete_ExistingId_RemovesTournament() throws SQLException {
        Tournament t = createTournament("DeleteMe");
        assertTrue(tournamentDAO.delete(t.getId()));
        assertNull(tournamentDAO.findById(t.getId()));
    }

    @Test
    void tournamentDelete_NonExistentId_ReturnsFalse() throws SQLException {
        assertFalse(tournamentDAO.delete(999L));
    }

    // ===================================================================
    // RoundDAO — sequential creation per tournament
    // ===================================================================

    @Test
    void createNext_FirstRoundForTournament_HasRoundNumberOne() throws SQLException {
        long tournamentId = createTournament("T").getId();
        long roundId = roundDAO.createNext(tournamentId);
        assertTrue(roundId > 0);
        assertEquals(1, roundCountFor(tournamentId));
    }

    @Test
    void createNext_TwoRoundsForSameTournament_NumbersAreOneAndTwo() throws SQLException {
        long tournamentId = createTournament("T").getId();
        long r1 = roundDAO.createNext(tournamentId);
        assertEquals(1, roundCountFor(tournamentId));
        long r2 = roundDAO.createNext(tournamentId);
        assertEquals(2, roundCountFor(tournamentId));
        // r1 and r2 are distinct ids
        assertTrue(r1 != r2);
        // verify round numbers via direct query
        assertEquals(1, roundNumberFor(tournamentId, r1));
        assertEquals(2, roundNumberFor(tournamentId, r2));
    }

    @Test
    void createNext_TwoTournaments_EachStartsAtOne() throws SQLException {
        long t1Id = createTournament("A").getId();
        long t2Id = createTournament("B").getId();
        roundDAO.createNext(t1Id);
        roundDAO.createNext(t2Id);
        // each tournament has exactly 1 round
        assertEquals(1, roundCountFor(t1Id));
        assertEquals(1, roundCountFor(t2Id));
    }

    // ===================================================================
    // MatchDAO — create with/without round, with/without winner
    // ===================================================================

    @Test
    void create_WithRound_Player1_Player2_Winner_ReturnsMatchId() throws SQLException {
        long roundId = createRound(createTournament("T").getId());
        long p1 = createPlayer("A").getId();
        long p2 = createPlayer("B").getId();
        long winner = createPlayer("C").getId(); // weird but fine for the test
        long matchId = matchDAO.create(roundId, p1, p2, winner);
        assertTrue(matchId > 0);
        assertTrue(matchExists(matchId));
    }

    @Test
    void create_WithoutRound_CasualMatch_RoundIdIsNullInDb() throws SQLException {
        long p1 = createPlayer("A").getId();
        long p2 = createPlayer("B").getId();
        long matchId = matchDAO.create(null, p1, p2, null);
        assertTrue(matchId > 0);
        assertTrue(matchExists(matchId));
        // verify round_id is null via direct query
        assertNull(roundIdFor(matchId));
    }

    @Test
    void create_Draw_WinnerIdIsNullInDb() throws SQLException {
        long roundId = createRound(createTournament("T").getId());
        long p1 = createPlayer("A").getId();
        long p2 = createPlayer("B").getId();
        long matchId = matchDAO.create(roundId, p1, p2, null);
        assertTrue(matchId > 0);
        assertNull(winnerIdFor(matchId));
    }

    // ===================================================================
    // MoveDAO — create + findByMatch
    // ===================================================================

    @Test
    void create_ReturnsMoveWithId() throws SQLException {
        long matchId = createMatch(null,
                createPlayer("A").getId(),
                createPlayer("B").getId(), null);
        Move m = moveDAO.create(matchId, createPlayer("C").getId(), 4, 1);
        assertTrue(m.getId() > 0);
        assertEquals(matchId, m.getMatchId());
        assertEquals(4, m.getBoardPosition());
        assertEquals(1, m.getMoveNumber());
    }

    @Test
    void findByMatch_ReturnsMovesInMoveNumberOrder() throws SQLException {
        long roundId = createRound(createTournament("T").getId());
        long matchId = createMatch(roundId,
                createPlayer("A").getId(),
                createPlayer("B").getId(), null);
        long pA = createPlayer("AP").getId();
        long pB = createPlayer("BP").getId();
        moveDAO.create(matchId, pA, 4, 3);
        moveDAO.create(matchId, pB, 0, 1);
        moveDAO.create(matchId, pA, 8, 2);
        List<Move> moves = moveDAO.findByMatch(matchId);
        assertEquals(3, moves.size());
        assertEquals(1, moves.get(0).getMoveNumber());
        assertEquals(2, moves.get(1).getMoveNumber());
        assertEquals(3, moves.get(2).getMoveNumber());
    }

    @Test
    void createDuplicateMoveNumberForSameMatch_ThrowsSQLException() throws SQLException {
        long matchId = createMatch(null,
                createPlayer("A").getId(),
                createPlayer("B").getId(), null);
        moveDAO.create(matchId, createPlayer("C").getId(), 4, 1);
        assertThrows(SQLException.class,
                () -> moveDAO.create(matchId, createPlayer("D").getId(), 1, 1));
    }

    // ===================================================================
    // FK / Cascade behaviour
    // ===================================================================

    @Test
    void deleteTournament_CascadesToRoundsMatchesAndMoves() throws SQLException {
        Tournament t = createTournament("Cascade");
        long tId = t.getId();
        long roundId = createRound(tId);
        long p1 = createPlayer("A").getId();
        long p2 = createPlayer("B").getId();
        long matchId = createMatch(roundId, p1, p2, null);
        createMove(matchId, p1, 0, 1);
        createMove(matchId, p2, 1, 2);

        tournamentDAO.delete(tId);

        assertEquals(0, roundCountFor(tId));
        assertNull(tournamentDAO.findById(tId));
        assertFalse(matchExists(matchId));
        assertTrue(moveDAO.findByMatch(matchId).isEmpty());
    }

    @Test
    void deletePlayerReferencedByMatch_ThrowsSQLException() throws SQLException {
        long p = createPlayer("Locked").getId();
        createMatch(null, p, createPlayer("B").getId(), null);
        assertThrows(SQLException.class, () -> playerDAO.delete(p));
    }

    @Test
    void deletePlayerWithNoMatches_Succeeds() throws SQLException {
        long p = createPlayer("Orphan").getId();
        assertTrue(playerDAO.delete(p));
        assertNull(playerDAO.findById(p));
    }

    // ---- direct-query helpers for FK tests ----

    private int roundNumberFor(long tournamentId, long roundId) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT round_number FROM rounds WHERE id = ? AND tournament_id = ?")) {
            stmt.setLong(1, roundId);
            stmt.setLong(2, tournamentId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private Long roundIdFor(long matchId) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT round_id FROM matches WHERE id = ?")) {
            stmt.setLong(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1) != null ? rs.getLong(1) : null;
                }
                return null;
            }
        }
    }

    private Long winnerIdFor(long matchId) throws SQLException {
        try (Connection conn = database.open();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT winner_id FROM matches WHERE id = ?")) {
            stmt.setLong(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1) != null ? rs.getLong(1) : null;
                }
                return null;
            }
        }
    }
}
