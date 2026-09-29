package TicTacToe.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest {

    private final WinChecker winChecker = new WinChecker();

    // ---- Preconditions: fresh session state ----

    @Test
    void newSession_BoardEmpty_CurrentPlayerIsX_GameNotOver() {
        GameSession s = new GameSession(1L, "Alice", 2L, "Bob", winChecker);
        assertFalse(s.isComplete());
        assertEquals('X', s.currentPlayer());
        assertEquals(0, s.moves().size());
    }

    @Test
    void newSession_PlayerMapping_ReturnsCorrectIds() {
        GameSession s = new GameSession(10L, "X", 20L, "O", winChecker);
        assertEquals(10L, s.playerFor('X'));
        assertEquals(20L, s.playerFor('O'));
    }

    @Test
    void newSession_PlayerNameMapping_ReturnsCorrectNames() {
        GameSession s = new GameSession(1L, "Alice", 2L, "Bob", winChecker);
        assertEquals("Alice", s.playerNameFor('X'));
        assertEquals("Bob", s.playerNameFor('O'));
    }

    // ---- Turn order ----

    @Test
    void afterXPlays_CurrentPlayerBecomesO() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0);
        assertEquals('O', s.currentPlayer());
    }

    @Test
    void afterOPlays_CurrentPlayerBecomesX() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); // X
        s.play(1); // O
        assertEquals('X', s.currentPlayer());
    }

    // ---- Occupied cell rejection ----

    @Test
    void playOnOccupiedCell_ReturnsFalse_BoardUnchanged() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); // X takes position 0
        boolean result = s.play(0); // O tries same cell -> rejected
        assertFalse(result);
        assertEquals(1, s.moves().size());
    }

    // ---- Out-of-range rejection ----

    @Test
    void playNegativePosition_ReturnsFalse() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        assertFalse(s.play(-1));
        assertEquals(0, s.moves().size());
    }

    @Test
    void playPosition9_ReturnsFalse() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        assertFalse(s.play(9));
        assertEquals(0, s.moves().size());
    }

    // ---- Win for X ends the game ----

    @Test
    void xWinsDiagonal0_4_8_GameOver_XIsWinner() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); // X
        s.play(1); // O
        s.play(4); // X
        s.play(2); // O
        boolean lastMove = s.play(8); // X wins
        assertTrue(lastMove);
        assertTrue(s.isComplete());
        assertEquals('X', s.winner());
        assertFalse(s.isDraw());
        assertEquals(5, s.moves().size());
    }

    // ---- Win for O ends the game ----

    @Test
    void oWinsRow0_GameOver_OEIsWinner() {
        // O wins row 0 (positions 0,1,2). X plays in rows 1-2
        // with no line of its own (positions 3,5,7 — not collinear).
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(3); // X
        s.play(0); // O
        s.play(5); // X
        s.play(1); // O
        s.play(7); // X
        s.play(2); // O wins row 0
        assertTrue(s.isComplete());
        assertEquals('O', s.winner());
    }

    // ---- Draw: board full, no winner ----

    @Test
    void boardFullNoWinner_Draw_GameOver() {
        // Produces:
        // X X O
        // O O X
        // X O X
        // a verified draw — 5 X's, 4 O's, no line of 3.
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); // X
        s.play(2); // O
        s.play(1); // X
        s.play(3); // O
        s.play(5); // X
        s.play(4); // O
        s.play(6); // X
        s.play(7); // O
        s.play(8); // X
        assertTrue(s.isComplete());
        assertNull(s.winner());
        assertTrue(s.isDraw());
    }

    // ---- Play after game over rejected (win) ----

    @Test
    void playAfterWin_ReturnsFalse_NoNewMoveRecorded() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); // X
        s.play(1); // O
        s.play(4); // X
        s.play(2); // O
        s.play(8); // X wins
        assertTrue(s.isComplete());
        int movesAtEnd = s.moves().size();
        assertFalse(s.play(5)); // X tries another move after game over
        assertEquals(movesAtEnd, s.moves().size());
    }

    // ---- Play after game over rejected (draw) ----

    @Test
    void playAfterDraw_ReturnsFalse_NoNewMoveRecorded() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        for (int pos = 0; pos < 9; pos++) {
            s.play(pos);
        }
        assertTrue(s.isComplete());
        int movesAtEnd = s.moves().size();
        assertFalse(s.play(0)); // try to play again
        assertEquals(movesAtEnd, s.moves().size());
    }

    // ---- Reset after game over ----

    @Test
    void resetAfterGameOver_BoardEmpty_CurrentPlayerIsX_GameNotOver() {
        GameSession s = new GameSession(1L, "X", 2L, "O", winChecker);
        s.play(0); s.play(1);
        s.play(4); s.play(2);
        s.play(8); // X wins
        assertTrue(s.isComplete());

        s.reset();

        assertFalse(s.isComplete());
        assertEquals('X', s.currentPlayer());
        assertEquals(0, s.moves().size());
    }

    @Test
    void eachMove_RecordsCorrectPlayerAndPosition() {
        GameSession s = new GameSession(10L, "Alice", 20L, "Bob", winChecker);
        s.play(4); // X
        assertEquals(10L, s.moves().get(0).getPlayerId());
        assertEquals(4, s.moves().get(0).getBoardPosition());
        assertEquals(1, s.moves().get(0).getMoveNumber());

        s.play(0); // O
        assertEquals(20L, s.moves().get(1).getPlayerId());
        assertEquals(0, s.moves().get(1).getBoardPosition());
        assertEquals(2, s.moves().get(1).getMoveNumber());
    }
}
