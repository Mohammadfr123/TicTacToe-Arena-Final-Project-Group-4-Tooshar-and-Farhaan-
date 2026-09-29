package TicTacToe.game;

import TicTacToe.model.Move;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    public static final char EMPTY = ' ';

    private final WinChecker winChecker;
    private final long playerXId;
    private final long playerOId;
    private final String playerXName;
    private final String playerOName;

    private char[] board;
    private char currentPlayer;
    private int moveCount;
    private boolean gameOver;
    private List<Move> moves;

    public GameSession(long playerXId, String playerXName,
                       long playerOId, String playerOName,
                       WinChecker winChecker) {
        this.playerXId = playerXId;
        this.playerXName = playerXName;
        this.playerOId = playerOId;
        this.playerOName = playerOName;
        this.winChecker = winChecker;

        this.board = new char[] {
                EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY
        };
        this.currentPlayer = 'X';
        this.moveCount = 0;
        this.gameOver = false;
        this.moves = new ArrayList<>();
    }

    public boolean play(int boardPosition) {
        if (gameOver) {
            return false;
        }
        if (boardPosition < 0 || boardPosition > 8) {
            return false;
        }
        if (board[boardPosition] != EMPTY) {
            return false;
        }

        char mark = currentPlayer;
        board[boardPosition] = mark;
        moveCount++;

        long playerId = (mark == 'X') ? playerXId : playerOId;
        Move move = new Move(-1, -1, playerId, boardPosition, moveCount);
        moves.add(move);

        if (winChecker.hasWinner(board, mark)) {
            gameOver = true;
            return true;
        }

        if (moveCount == 9) {
            gameOver = true;
            return true;
        }

        //switch player
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        return true;
    }

    public boolean isComplete() {
        return gameOver;
    }

    public boolean isDraw() {
        return gameOver && winner() == null;
    }

    public Character winner() {
        if (!gameOver) {
            return null;
        }
        if (winChecker.hasWinner(board, 'X')) {
            return 'X';
        }
        if (winChecker.hasWinner(board, 'O')) {
            return 'O';
        }
        return null;
    }

    public char currentPlayer() {
        return currentPlayer;
    }

    public long playerFor(char mark) {
        if (mark == 'X') {
            return playerXId;
        } else {
            return playerOId;
        }
    }

    public String playerNameFor(char mark) {
        if (mark == 'X') {
            return playerXName;
        } else {
            return playerOName;
        }
    }

    public List<Move> moves() {
        return moves;
    }

    public void reset() {
        this.board = new char[] {
                EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY
        };
        this.currentPlayer = 'X';
        this.moveCount = 0;
        this.gameOver = false;
        this.moves = new ArrayList<>();
    }
}

