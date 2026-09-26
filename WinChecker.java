package TicTacToe;

import java.util.ArrayList;
import java.util.List;

/**
 * WinChecker implements the recursive logic to detect a winner.
 * This is the core logic Tooshar was responsible for.
 */
public class WinChecker {

    /**
     * Checks if the given player has won the game.
     * @param board The 9-cell board state.
     * @param player The player to check ('X' or 'O').
     * @return true if the player won, false otherwise.
     */
    public boolean hasWinner(char[] board, char player) {
        // Define the 8 possible winning lines
        List<int[]> lines = new ArrayList<>();
        // Rows
        lines.add(new int[]{0, 1, 2});
        lines.add(new int[]{3, 4, 5});
        lines.add(new int[]{6, 7, 8});
        // Cols
        lines.add(new int[]{0, 3, 6});
        lines.add(new int[]{1, 4, 7});
        lines.add(new int[]{2, 5, 8});
        // Diagonals
        lines.add(new int[]{0, 4, 8});
        lines.add(new int[]{2, 4, 6});

        // RECURSION 1: Traverse the list of lines recursively
        return checkLinesRecursive(lines, 0, board, player);
    }

    private boolean checkLinesRecursive(List<int[]> lines, int lineIndex, char[] board, char player) {
        // Base case: If we checked all 8 lines and found no winner
        if (lineIndex >= lines.size()) {
            return false;
        }

        // Recursive Step: Check the current line
        if (checkCellsRecursive(lines.get(lineIndex), 0, board, player)) {
            return true; // Found a winning line!
        }

        // Base case: Current line failed, move to the next line recursively
        return checkLinesRecursive(lines, lineIndex + 1, board, player);
    }

    private boolean checkCellsRecursive(int[] line, int cellIndex, char[] board, char player) {
        // Base case: If we checked all 3 cells in the line and they all matched
        if (cellIndex >= 3) {
            return true;
        }

        // Base case: Current cell doesn't match the player
        if (board[line[cellIndex]] != player) {
            return false;
        }

        // Recursive Step: Current cell matched, check the next cell in the line
        return checkCellsRecursive(line, cellIndex + 1, board, player);
    }

    /**
     * Checks if the board is full (resulting in a draw).
     */
    public boolean isFull(char[] board) {
        for (char cell : board) {
            if (cell == ' ') return false;
        }
        return true;
    }
}
