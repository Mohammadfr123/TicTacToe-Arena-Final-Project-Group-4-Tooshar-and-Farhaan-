package TicTacToe.model;

public class Move {
    private long id;
    private long matchId;
    private long playerId;
    private int boardPosition
    private int moveNumber;

    public Move(long id, long matchId, long playerId, int boardPosition, int moveNumber) {
        this.id = id;
        this.matchId = matchId;
        this.playerId = playerId;
        this.boardPosition = boardPosition;
        this.moveNumber = moveNumber;
    }

    public long getId() {
        return id;
    }

    public long getMatchId() {
        return matchId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public int getBoardPosition() {
        return boardPosition;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
}
