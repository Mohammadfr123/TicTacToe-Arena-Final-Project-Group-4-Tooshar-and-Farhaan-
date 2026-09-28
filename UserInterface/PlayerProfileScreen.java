package TicTacToe.UserInterface;

import TicTacToe.model.Player;
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
import TicTacToe.dao.PlayerDAO;


/**
 * PlayerProfileScreen allows the user to manage registered players.
 * This implements full CRUD (Create, Read, Update, Delete).
 * It interacts with the PlayerDAO to persist data in the database.
 */
public class PlayerProfileScreen {

    public static Scene build(SceneManager sceneManager, PlayerDAO playerDAO) {
        // READ: We fetch all players from the database and put them in an ObservableList
        // so the ListView updates automatically when the list changes.
        ObservableList<Player> players = FXCollections.observableArrayList(playerDAO.findAll());
        ListView<Player> listView = new ListView<>(players);

        listView.getStyleClass().add("list-view");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        Button addBtn = new Button("Add Player");
        Button updateBtn = new Button("Update Selected");
        Button deleteBtn = new Button("Delete Selected");
        addBtn.getStyleClass().add("primary-button");
        updateBtn.getStyleClass().add("secondary-button");
        deleteBtn.getStyleClass().add("danger-button");

        // CREATE: Validate input, then call DAO to save to database
        addBtn.setOnAction(e -> {
            try {
                String username = Validator.requireNonBlankUsername(usernameField.getText());
                Player player = playerDAO.create(username);
                players.add(player);
                usernameField.clear();
            } catch (ValidationException ex) {
                DialogUtil.showError("Couldn't add player", ex.getMessage());
            }
        });

        // UPDATE: Update the username of the selected player in the DB
        updateBtn.setOnAction(e -> {
            Player selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                DialogUtil.showError("Nothing selected", "Select a player from the list first.");
                return;
            }
            try {
                String newUsername = Validator.requireNonBlankUsername(usernameField.getText());
                selected.setUsername(newUsername);
                playerDAO.update(selected);
                listView.refresh();
                usernameField.clear();
            } catch (ValidationException ex) {
                DialogUtil.showError("Couldn't update player", ex.getMessage());
            }
        });

        // DELETE: Remove the player from the database by ID
        deleteBtn.setOnAction(e -> {
            Player selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                DialogUtil.showError("Nothing selected", "Select a player from the list first.");
                return;
            }
            playerDAO.delete(selected.getId());
            players.remove(selected);
        });

        Button backBtn = new Button("Back to Menu");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> sceneManager.showMainMenu());

        Label title = new Label("Player Profiles");
        title.getStyleClass().add("section-label");

        HBox formRow = new HBox(10, usernameField, addBtn, updateBtn, deleteBtn);
        VBox card = new VBox(15, title, listView, formRow);
        card.getStyleClass().add("card");
        VBox root = new VBox(20, card, backBtn);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 520, 520);
        scene.getStylesheets().add(PlayerProfileScreen.class.getResource("/css/styles.css").toExternalForm());
        return scene;
    }
}
