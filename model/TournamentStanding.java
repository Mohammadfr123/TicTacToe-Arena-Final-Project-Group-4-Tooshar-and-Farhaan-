package TicTacToe.model;

public class TournamentStanding {
    private final String username;
    private final long played;
    private final long wins;
    private final long draws;
    private final long points;

    public TournamentStanding(String username, long played, long wins, long draws, long points) {
        this.username = username;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public long getPlayed() {
        return played;
    }

    public long getWins() {
        return wins;
    }

    public long getDraws() {
        return draws;
    }

    public long getPoints() {
        return points;
    }
}