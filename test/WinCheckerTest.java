package TicTacToe.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WinCheckerTest {

    private final WinChecker checker = new WinChecker();

    //9-cell board from a 9-character string. 'X' and 'O' become marks with every other character becomes empty
    private char[] board(String layout) {
        char[] b = new char[9];
        for (int i = 0; i < 9; i++) {
            char c = layout.charAt(i);
            b[i] = (c == 'X' || c == 'O') ? c : ' ';
        }
        return b;
    }

    // Winning line tests: X wins

    @Test void rowZeroComplete_XWins() {
        assertTrue(checker.hasWinner(board("XXX      "), 'X'));
    }
    @Test void rowOneComplete_XWins() {
        assertTrue(checker.hasWinner(board("   XXX   "), 'X'));
    }
    @Test void rowTwoComplete_XWins() {
        assertTrue(checker.hasWinner(board("      XXX"), 'X'));
    }
    @Test void colZeroComplete_XWins() {
        assertTrue(checker.hasWinner(board("X   X   X"), 'X'));
    }
    @Test void colOneComplete_XWins() {
        assertTrue(checker.hasWinner(board(" X  X  X "), 'X'));
    }
    @Test void colTwoComplete_XWins() {
        assertTrue(checker.hasWinner(board("  X  X  X"), 'X'));
    }
    @Test void diagonalTopLeftToBottomRight_XWins() {
        assertTrue(checker.hasWinner(board("X   X   X"), 'X'));
    }
    @Test void diagonalTopRightToBottomLeft_XWins() {
        assertTrue(checker.hasWinner(board("  X X X  "), 'X'));
    }

    //  Symmetry: O wins, X does not

    @Test
    void rowZeroComplete_OWins() {
        char[] b = board("OOO      ");
        assertTrue(checker.hasWinner(b, 'O'));
        assertFalse(checker.hasWinner(b, 'X'));
    }

    //  No winner cases

    @Test
    void boardWithNoCompleteLine_NoWinner() {
        char[] b = board("XOXOOXOXO");
        assertFalse(checker.hasWinner(b, 'X'));
        assertFalse(checker.hasWinner(b, 'O'));
    }

    @Test
    void boardWithXsButNoLine_NoWinner() {
        char[] b = board("X X   X  ");
        assertFalse(checker.hasWinner(b, 'X'));
    }

    //  Boundary: almost-complete line

    @Test
    void lineWithTwoOfThree_CountsAsNotComplete() {
        char[] b = board("XX        ");
        assertFalse(checker.hasWinner(b, 'X'));
        assertFalse(checker.hasWinner(b, 'O'));
    }

    @Test
    void lineBlockedByOtherPlayer_CountsAsNotComplete() {
        char[] b = board("XOX       ");
        assertFalse(checker.hasWinner(b, 'X'));
    }

    //  Boundary: full board

    @Test
    void fullBoardNoWinner_NoWinnerAndIsFull() {
        char[] b = board("XOXOOXOXO");
        assertFalse(checker.hasWinner(b, 'X'));
        assertFalse(checker.hasWinner(b, 'O'));
        assertTrue(checker.isFull(b));
    }

    @Test
    void fullBoardWithWinner_WinnerAndIsFull() {
        // Diagonal 0-4-8 all X, board full
        char[] b = board("XOOOXOOOX");
        assertTrue(checker.hasWinner(b, 'X'));
        assertTrue(checker.isFull(b));
    }

    //  Failure: candidate doesn't match

    @Test
    void winningBoardForX_WrongCandidateReturnsFalse() {
        char[] b = board("XXX      ");
        assertFalse(checker.hasWinner(b, 'O'));
    }

    @Test
    void boardWithOnlyO_CheckingXReturnsFalse() {
        char[] b = board("OOO       ");
        assertFalse(checker.hasWinner(b, 'X'));
    }

    //  isFull helper: boundary cases

    @Test
    void emptyBoard_IsNotFull() {
        assertFalse(checker.isFull(board("         ")));
    }

    @Test
    void oneCellFilled_IsNotFull() {
        assertFalse(checker.isFull(board("X         ")));
    }

    @Test
    void eightCellsFilled_IsNotFull() {
        assertFalse(checker.isFull(board("XOXOXOXO ")));
    }

    @Test
    void nineCellsFilled_IsFull() {
        assertTrue(checker.isFull(board("XOXOXOXOX")));
    }
}
