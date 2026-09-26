package TicTacToe.model;

/**
 * Represents a single Move made in a match.
 */
public class Move {
    private int id;
    private int matchId;
    private int playerId;
    private int boardPosition;
    private int moveNumber;

    public Move(int id, int matchId, int playerId, int boardPosition, int moveNumber) {
        this.id = id;
        this.matchId = matchId;
        this.playerId = playerId;
        this.boardPosition = boardPosition;
        this.moveNumber = moveNumber;
    }

    public int getId() { return id; }
    public int getMatchId() { return matchId; }
    public int getPlayerId() { return playerId; }
    public int getBoardPosition() { return boardPosition; }
    public int getMoveNumber() { return moveNumber; }
}
