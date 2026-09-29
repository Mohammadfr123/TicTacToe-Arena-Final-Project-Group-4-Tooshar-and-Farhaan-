package TicTacToe.UserInterface;
import TicTacToe.util.DialogUtil;

import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import TicTacToe.game.GameSession;
import TicTacToe.game.GameService;
import TicTacToe.game.WinChecker;
import javafx.scene.layout.VBox;
import TicTacToe.game.WinChecker;

public class GameBoardScreen {

    public static Scene build(SceneManager sceneManager, GameService gameService, long tournamentId, long playerXId, String playerXName, long playerOId, String playerOName) {
        char[] board = new char[9];
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
        }

        WinChecker winChecker = new WinChecker();
        GameSession session = new GameSession(playerXId, playerXName, playerOId, playerOName, winChecker);

        // Holds whose turn it is in a single-element array so the click handler lambdas below can read and update it.
        char[] currentPlayer = { 'X' };

        Label status = new Label(playerXName + "'s turn (" + 'X' + ")");
        status.getStyleClass().add("status-label");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(8);
        grid.setVgap(8);

        Button[] cells = new Button[9];
        for (int i = 0; i < 9; i++) {
            Button cell = new Button(" ");
            cell.getStyleClass().add("cell-button");
            cell.setPrefSize(90, 90);
            final int index = i;
            cell.setOnAction(e ->
                    handleCellClick(index, session, gameService, tournamentId, cells, status, winChecker));
            cells[i] = cell;
            grid.add(cell, i % 3, i / 3);
        }

        Button resetBtn = new Button("New Game");
        resetBtn.getStyleClass().add("secondary-button");
        // rebuilds a fresh board
        resetBtn.setOnAction(e -> {
            session.reset();
            for (Button c : cells) {
                c.setText(" ");
                c.getStyleClass().removeAll("cell-x", "cell-o");
            }
            status.setText(playerXName + "'s turn (" + 'X' + ")");
        });

        Button backBtn = new Button("Back to Menu");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> sceneManager.showMainMenu());

        VBox card = new VBox(18, status, grid, resetBtn);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card");

        VBox root = new VBox(20, card, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 460, 560);
        scene.getStylesheets().add(GameBoardScreen.class.getResource("/css/styles.css").toExternalForm());
        return scene;
    }

    private static void handleCellClick(int index, GameSession session, GameService gameService, long tournamentId, Button[] cells, Label status, WinChecker winChecker) {
        char playerMark = session.currentPlayer(); // whose turn it is right now
        boolean moved = session.play(index);
        if (!moved) {
            return; // ignore: game over, invalid position, or cell already filled
        }

        // Update the cell the player just marked
        cells[index].setText(String.valueOf(playerMark));
        cells[index].getStyleClass().add(playerMark == 'X' ? "cell-x" : "cell-o");

        if (session.isComplete()) {
            endGame(status, session, gameService, tournamentId, cells);
        } else {
            char next = session.currentPlayer();
            status.setText(session.playerNameFor(next) + "'s turn (" + next + ")");
        }
    }


    private static void endGame(Label status, GameSession session, GameService gameService, long tournamentId, Button[] cells) {
        String result;
        Character winner = session.winner();
        if (winner != null) {
            result = session.playerNameFor(winner) + " wins!";
        } else {
            result = "It's a draw!";
        }
        status.setText(result);

        // Persist round + match + moves
        try {
            gameService.saveCompleted(session, tournamentId);
        } catch (SQLException e) {
            DialogUtil.showError("Save error",
                    "Game finished but could not save to the database:\n" + e.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, result);
        alert.setHeaderText("Game over");
        alert.showAndWait();
    }

}
