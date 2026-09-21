package TicTacToe.UserInterface;

//import TicTacToe.dao.PlayerDAO;  i'll add when PlayerDAO is finished
//import TicTacToe.dao.TournamentDAO; i'll add when Tournament.DAO is finished
import javafx.scene.Scene;
import javafx.stage.Stage;

/*navigation controller to navigate between screens).*/

public class SceneManager {

    private final Stage stage;
    private final PlayerDAO playerDAO;
    private final TournamentDAO tournamentDAO;

    public SceneManager(Stage stage, PlayerDAO playerDAO, TournamentDAO tournamentDAO) {
        this.stage = stage;
        this.playerDAO = playerDAO;
        this.tournamentDAO = tournamentDAO;
    }

    public void showMainMenu() {
        stage.setScene(MainMenuScreen.build(this));
    }

    public void showGameBoard() {
        stage.setScene(GameBoardScreen.build(this));
    }

    public void showPlayerProfiles() {
        stage.setScene(PlayerProfileScreen.build(this, playerDAO));
    }

    public void showTournaments() {
        stage.setScene(TournamentScreen.build(this, tournamentDAO));
    }

    public void exit() {
        stage.close();
    }
}
