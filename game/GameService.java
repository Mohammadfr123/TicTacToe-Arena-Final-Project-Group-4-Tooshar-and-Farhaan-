pakage TicTacToe.game;

import TicTacToe.dao.MatchDAO;
import TicTacToe.dao.MoveDAO;
import TicTacToe.dao.RoundDAO;
import TicTacToe.model.Move;

import java.sql.SQLException;
import java.util.List;

public class GameService {
    private final MatchDAO matches;
    private final MoveDAO moves;
    private final RoundDAO rounds;

    public GameService(MatchDAO matches, MoveDAO moves, RoundDAO rounds) {
        this.matches = matches;
        this.moves = moves;
        this.rounds = rounds;
    }

    public long saveCompleted(GameSession session, Long tournamentId)
            throws SQLException {
        // 1. Create round if this is a tournament match
        Long roundId = (tournamentId == null) ? null : rounds.createNext(tournamentId);

        // 2. Determine winner id (null for draw)
        Long winnerId = null;
        Character winner = session.winner();
        if (winner != null) {
            winnerId = session.playerFor(winner);
        }

        // 3. Create the match
        long matchId = matches.create(roundId,
                session.playerFor('X'),
                session.playerFor('O'),
                winnerId);

        // 4. Persist each move
        List<Move> sessionMoves = session.moves();
        for (Move move : sessionMoves) {
            moves.create(matchId,
                    move.getPlayerId(),
                    move.getBoardPosition(),
                    move.getMoveNumber());
        }

        return matchId;
    }
}
