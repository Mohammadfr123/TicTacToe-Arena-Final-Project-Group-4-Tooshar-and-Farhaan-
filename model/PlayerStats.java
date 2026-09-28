public class PlayerStats {
    private final long gamesPlayed;
    private final long wins;
    private final long draws;

    public PlayerStats(long gamesPlayed, long wins, long draws) {
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.draws = draws;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public long getWins() {
        return wins;
    }

    public long getDraws() {
        return draws;
    }

    //No duplication of data
    public long losses() {
        return gamesPlayed - wins - draws;
    }
}