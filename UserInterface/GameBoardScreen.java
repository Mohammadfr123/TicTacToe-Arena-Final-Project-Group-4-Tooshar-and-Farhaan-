package TicTacToe.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GameBoardScreen {

    public static Scene build(SceneManager sceneManager) {
        char[] board = new char[9];
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
        }

        WinChecker winChecker = new WinChecker();

        // Holds whose turn it is in a single-element array so the click handler lambdas below can read and update it.
        char[] currentPlayer = { 'X' };

        Label status = new Label("Player X's turn");
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
                    handleCellClick(index, board, cells, status, currentPlayer, winChecker));
            cells[i] = cell;
            grid.add(cell, i % 3, i / 3);
        }

        Button resetBtn = new Button("New Game");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setOnAction(e -> sceneManager.showGameBoard()); // rebuilds a fresh board

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

    private static void handleCellClick(int index, char[] board, Button[] cells, Label status,
                                          char[] currentPlayer, WinChecker winChecker) {
        if (board[index] != ' ') {
            return; // ignore clicks on an already-filled cell
        }

        char player = currentPlayer[0];
        board[index] = player;
        cells[index].setText(String.valueOf(player));
        cells[index].getStyleClass().add(player == 'X' ? "cell-x" : "cell-o");

        if (winChecker.hasWinner(board, player)) {
            endGame(status, "Player " + player + " wins!");
            return;
        }
        if (winChecker.isFull(board)) {
            endGame(status, "It's a draw!");
            return;
        }

        // Switch turns.
        currentPlayer[0] = (player == 'X') ? 'O' : 'X';
        status.setText("Player " + currentPlayer[0] + "'s turn");
    }

    private static void endGame(Label status, String result) {
        status.setText(result);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, result);
        alert.setHeaderText("Game over");
        alert.showAndWait();
    }
}
