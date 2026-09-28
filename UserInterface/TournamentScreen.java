package TicTacToe.UserInterface;

import TicTacToe.model.Tournament;
import TicTacToe.util.DialogUtil;
import TicTacToe.util.ValidationException;
import TicTacToe.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import TicTacToe.dao.TournamentDAO;

/**
 * TournamentScreen allows users to create and view tournaments.
 * This implements basic CRUD for the Tournament entity.
 * It interacts with the TournamentDAO to persist data.
 */
public class TournamentScreen {

    public static Scene build(SceneManager sceneManager, TournamentDAO tournamentDAO) {
        // READ: Load all existing tournaments from the database
        ObservableList<Tournament> tournaments = FXCollections.observableArrayList(tournamentDAO.findAll());
        ListView<Tournament> listView = new ListView<>(tournaments);
        listView.getStyleClass().add("list-view");

        TextField nameField = new TextField();
        nameField.setPromptText("Tournament name");
        nameField.getStyleClass().add("text-field");

        Button createBtn = new Button("Create Tournament");
        Button deleteBtn = new Button("Delete Selected");
        createBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        // Standings label acts as a placeholder for detailed match/round data
        Label standings = new Label(
                "Select a tournament to view standings.\n" +
                "(Standings populate once Match/Round data is wired up through the database.)"
        );
        standings.setWrapText(true);
        standings.getStyleClass().add("hint-label");

        // CREATE: Validate the name and save the tournament via DAO
        createBtn.setOnAction(e -> {
            try {
                String name = Validator.requireNonBlankTournamentName(nameField.getText());
                Tournament tournament = tournamentDAO.create(name);
                tournaments.add(tournament);
                nameField.clear();
            } catch (ValidationException ex) {
                DialogUtil.showError("Couldn't create tournament", ex.getMessage());
            }
        });

        // DELETE: Remove the selected tournament from the database
        deleteBtn.setOnAction(e -> {
            Tournament selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                DialogUtil.showError("Nothing selected", "Select a tournament from the list first.");
                return;
            }
            tournamentDAO.delete(selected.getId());
            tournaments.remove(selected);
        });

        // Interaction: Update the standings text when a tournament is selected
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                standings.setText("Standings for \"" + newVal.getName() + "\" will appear here.");
            }
        });

        Button backBtn = new Button("Back to Menu");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> sceneManager.showMainMenu());

        Label title = new Label("Tournaments");
        title.getStyleClass().add("section-label");

        HBox formRow = new HBox(10, nameField, createBtn, deleteBtn);
        VBox card = new VBox(15, title, listView, formRow, standings);
        card.getStyleClass().add("card");
        VBox root = new VBox(20, card, backBtn);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 540, 560);
        scene.getStylesheets().add(TournamentScreen.class.getResource("/css/styles.css").toExternalForm());
        return scene;
    }
}
