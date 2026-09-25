package TicTacToe.UserInterface;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SceneManager acts as the "Router" or "Controller" for the application.
 * Its primary job is to switch between different screens (Scenes) 
 * and hold references to the Data Access Objects (DAOs) so they can be 
 * passed to the screens that need them.
 */
public class SceneManager {

    private final Stage stage;
    // These are the bridges to the database. Tooshar implements these.
    private final PlayerDAO playerDAO;
    private final TournamentDAO tournamentDAO;

    public SceneManager(Stage stage, PlayerDAO playerDAO, TournamentDAO tournamentDAO) {
        this.stage = stage;
        this.playerDAO = playerDAO;
        this.tournamentDAO = tournamentDAO;
    }

    /**
     * Transitions the app to the Main Menu.
     */
    public void showMainMenu() {
        stage.setScene(MainMenuScreen.build(this));
    }

    /**
     * Transitions the app to the Game Board for a match.
     */
    public void showGameBoard() {
        stage.setScene(GameBoardScreen.build(this));
    }

    /**
     * Transitions the app to Player Profiles management.
     * We pass the playerDAO here so the screen can create/update/delete players.
     */
    public void showPlayerProfiles() {
        stage.setScene(PlayerProfileScreen.build(this, playerDAO));
    }

    /**
     * Transitions the app to Tournament management.
     * We pass the tournamentDAO here so the screen can manage tournaments.
     */
    public void showTournaments() {
        stage.setScene(TournamentScreen.build(this, tournamentDAO));
    }

    /**
     * Closes the application window.
     */
    public void exit() {
        stage.close();
    }
}
