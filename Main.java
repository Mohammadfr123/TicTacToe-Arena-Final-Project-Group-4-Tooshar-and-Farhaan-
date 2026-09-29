package TicTacToe;

import TicTacToe.db.Database;
import TicTacToe.dao.PlayerDAO;
import TicTacToe.dao.TournamentDAO;
import TicTacToe.dao.RoundDAO;
import TicTacToe.dao.MatchDAO;
import TicTacToe.dao.MoveDAO;
import TicTacToe.UserInterface.SceneManager;
import TicTacToe.util.DialogUtil;
import TicTacToe.game.GameService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    private Database database;
    private PlayerDAO playerDAO;
    private TournamentDAO tournamentDAO;
    private RoundDAO roundDAO;
    private MatchDAO matchDAO;
    private MoveDAO moveDAO;
    private SceneManager sceneManager;
    private GameService gameService;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Database - configured() reads -Dtictactoe.db or defaults to ~/.tictactoe-arena/arena.db
            database = Database.configured();

            // 2. Initialize - creates the parent directory if needed, then runs
            database.initialize();

            // 3. All 5 DAOs. PlayerDAO + TournamentDAO are passed to SceneManager
            playerDAO = new PlayerDAO(database);
            tournamentDAO = new TournamentDAO(database);
            roundDAO = new RoundDAO(database);
            matchDAO = new MatchDAO(database);
            moveDAO = new MoveDAO(database);

            //4. Game Service for Tournament
            gameService = new GameService(matchDAO, moveDAO, roundDAO);

            // 5. SceneManager - receives the primaryStage
            sceneManager = new SceneManager(primaryStage, playerDAO, tournamentDAO, gameService);

            // 6. Show the main menu - the first screen the user sees.
            primaryStage.show(); //To show the stage
            sceneManager.showMainMenu();

        } catch (SQLException | IOException e) {
           //Databse error handling
            DialogUtil.showError(
                    "Database error",
                    "Could not start the application:\n" + e.getMessage());
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}