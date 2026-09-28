package TicTacToe.model;

public class Round {
    private long id;
    private long tournamentId;
    private int roundNumber;

    public Round(long id, long tournamentId, int roundNumber) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.roundNumber = roundNumber;
    }

    public long getId() {
        return id;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}

