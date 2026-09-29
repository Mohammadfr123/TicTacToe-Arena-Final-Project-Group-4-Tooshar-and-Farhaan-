package TicTacToe.UserInterface;

import TicTacToe.model.Tournament;
import TicTacToe.util.DialogUtil;
import TicTacToe.util.ValidationException;
import TicTacToe.util.Validator;
import java.sql.SQLException;
import java.util.List;
import TicTacToe.model.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import TicTacToe.dao.TournamentDAO;
import TicTacToe.dao.PlayerDAO;
import TicTacToe.model.TournamentStanding;


/**
 * TournamentScreen allows users to create and view tournaments.
 * This implements basic CRUD for the Tournament entity.
 * It interacts with the TournamentDAO to persist data.
 */
public class TournamentScreen {

    public static Scene build(SceneManager sceneManager, TournamentDAO tournamentDAO, PlayerDAO playerDAO) {
        // READ: Load all existing tournaments from the database

        final ObservableList<Tournament> tournaments = FXCollections.observableArrayList();
        try {
            tournaments.addAll(tournamentDAO.findAll());
        } catch (SQLException e) {
            DialogUtil.showError("Database error", "Could not load tournaments: " + e.getMessage());
        }

        ListView<Tournament> listView = new ListView<>(tournaments);
        listView.getStyleClass().add("list-view");

        // Choosing Player for the match
        Label pickLabel = new Label("Pick two players for the match:");
        pickLabel.getStyleClass().add("hint-label");

        ObservableList<Player> playerList = FXCollections.observableArrayList();
        try {
            playerList.addAll(playerDAO.findAll());
        } catch (SQLException e) {
            DialogUtil.showError("Database error", "Could not load players: " + e.getMessage());
        }

        ComboBox<Player> playerXCombo = new ComboBox<>();  // empty constructor
        playerXCombo.setPromptText("Player X (goes first)");
        playerXCombo.setPrefWidth(160);

        ComboBox<Player> playerOCombo = new ComboBox<>();  // empty constructor
        playerOCombo.setPromptText("Player O");
        playerOCombo.setPrefWidth(160);

        //populating the list
        playerXCombo.getItems().addAll(playerList);
        playerOCombo.getItems().addAll(playerList);


        TextField nameField = new TextField();
        nameField.setPromptText("Tournament name");
        nameField.getStyleClass().add("text-field");

        Button createBtn = new Button("Create Tournament");
        Button deleteBtn = new Button("Delete Selected");
        Button startMatchBtn = new Button("Start Match");
        createBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        // START MATCH: Launch a game inside the selected tournament
        startMatchBtn.getStyleClass().add("primary-button");
        startMatchBtn.setOnAction(e -> {
            Tournament selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                DialogUtil.showError("No tournament selected",
                        "Select a tournament first.");
                return;
            }
            try {
                // Load all players and pick the first two
                List<Player> allPlayers = playerDAO.findAll();
                if (allPlayers.size() < 2) {
                    DialogUtil.showError("Not enough players",
                            "You need at least 2 registered players to start a match.");
                    return;
                }

                playerXCombo.getItems().addAll(playerList);
                playerOCombo.getItems().addAll(playerList);

                Player playerX = playerXCombo.getValue();
                Player playerO = playerOCombo.getValue();
                if (playerX == null || playerO == null) {
                    DialogUtil.showError("Players not selected",
                            "Pick both Player X and Player O from the dropdowns.");
                    return;
                }
                if (playerX.getId() == playerO.getId()) {
                    DialogUtil.showError("Same player",
                            "Player X and Player O must be different.");
                    return;
                }
                sceneManager.showTournamentMatch(selected.getId(), playerX.getId(), playerX.getUsername(), playerO.getId(), playerO.getUsername());

            } catch (SQLException ex) {
                DialogUtil.showError("Database error",
                        "Could not load players: " + ex.getMessage());
            }
        });


        // Standings compute from database when tournament is selected
        Label standings = new Label(
                "Select a tournament to view standings.\n" +
                "(Standings populate once Match/Round data is wired up through the database.)"
        );
        standings.setWrapText(true);
        standings.getStyleClass().add("hint-label");

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    List<TournamentStanding> standingsList = tournamentDAO.standings(newVal.getId());
                    if (standingsList.isEmpty()) {
                        standings.setText("No matches played in this tournament yet.");
                    } else {
                        StringBuilder sb = new StringBuilder("Standings:\n\n");
                        for (TournamentStanding s : standingsList) {
                            sb.append(String.format("%-14s  %2d games  %2d wins  %2d draws  %3d pts\n",
                                    s.getUsername(),
                                    s.getPlayed(),
                                    s.getWins(),
                                    s.getDraws(),
                                    s.getPoints()));
                        }
                        standings.setText(sb.toString());
                    }
                } catch (SQLException ex) {
                    standings.setText("Could not load standings: " + ex.getMessage());
                }
            } else {
                standings.setText("Select a tournament to view standings.");
            }
        });


        // CREATE: Validate the name and save the tournament via DAO
        createBtn.setOnAction(e -> {
            try {
                String name = Validator.requireNonBlankTournamentName(nameField.getText());
                Tournament tournament = tournamentDAO.create(name);
                tournaments.add(tournament);
                nameField.clear();
            } catch (ValidationException ex) {
                DialogUtil.showError("Couldn't create tournament", ex.getMessage());
            } catch (SQLException ex) {
                DialogUtil.showError("Database error", "Could not create tournament: " + ex.getMessage());
            }
        });

        // DELETE: Remove the selected tournament from the database
        deleteBtn.setOnAction(e -> {
            Tournament selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                DialogUtil.showError("Nothing selected", "Select a tournament from the list first.");
                return;
            }
            try {
                tournamentDAO.delete(selected.getId());
                tournaments.remove(selected);
            } catch (SQLException ex) {
                DialogUtil.showError("Database error", "Could not delete tournament: " + ex.getMessage());
            }
        });

        Button backBtn = new Button("Back to Menu");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> sceneManager.showMainMenu());

        Label title = new Label("Tournaments");
        title.getStyleClass().add("section-label");

        HBox formRow = new HBox(10, nameField, createBtn, deleteBtn, startMatchBtn);
        HBox pickRow = new HBox(10, pickLabel, playerXCombo, playerOCombo);
        VBox card = new VBox(15, title, listView, formRow, pickRow ,standings);
        card.getStyleClass().add("card");
        VBox root = new VBox(20, card, backBtn);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 540, 560);
        scene.getStylesheets().add(TournamentScreen.class.getResource("/css/styles.css").toExternalForm());
        return scene;
    }
}
