package TicTacToe.game;

public final class WinChecker {
    private static final int[][] LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, //row
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, //colum
            {0, 4, 8}, {2, 4, 6} //diagonal
    };

    public boolean hasWinner(char[] board, char candidate) {
        return scanLines(board, candidate, 0);
    }

    private boolean scanLines(char[] board, char candidate, int lineIndex) {
        if (lineIndex == LINES.length) {
            return false;
        }
        return lineMatches(board, candidate, LINES[lineIndex], 0)
                || scanLines(board, candidate, lineIndex + 1);
    }

    private boolean lineMatches(char[] board, char candidate, int[] line, int positionIndex) {
        if (positionIndex == line.length) {
            return true;
        }
        if (board[line[positionIndex]] != candidate) {
            return false;
        }
        return lineMatches(board, candidate, line, positionIndex + 1);
    }

    public boolean isFull(char[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == EMPTY) {
                return false;
            }
        }
        return true;
    }
}

