package TicTacToe.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * MainMenuScreen is the entry point of the application.
 * It provides navigation to the three main areas of the app:
 * 1. New Game (GameBoardScreen)
 * 2. Tournaments (TournamentScreen)
 * 3. Player Profiles (PlayerProfileScreen)
 */
public class MainMenuScreen {

    public static Scene build(SceneManager sceneManager) {
        Label title = new Label("TicTacTac Arena");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Two players. One board. Real history.");
        subtitle.getStyleClass().add("hint-label");

        // Buttons for primary navigation
        Button newGameBtn = new Button("New Game");
        Button tournamentBtn = new Button("Tournament");
        Button profilesBtn = new Button("Player Profiles");
        Button exitBtn = new Button("Exit");

        // Apply consistent styling to all menu buttons
        for (Button b : new Button[]{newGameBtn, tournamentBtn, profilesBtn, exitBtn}) {
            b.getStyleClass().add("menu-button");
            b.setMaxWidth(240);
            b.setPrefHeight(44);
        }

        // event handlers link the buttons to SceneManager methods
        newGameBtn.setOnAction(e -> sceneManager.showGameBoard());
        tournamentBtn.setOnAction(e -> sceneManager.showTournaments());
        profilesBtn.setOnAction(e -> sceneManager.showPlayerProfiles());
        exitBtn.setOnAction(e -> sceneManager.exit());

        // Layout using a Vertical Box (VBox) to stack elements centered
        VBox root = new VBox(14, title, subtitle, newGameBtn, tournamentBtn, profilesBtn, exitBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("root");
        VBox.setMargin(subtitle, new Insets(0, 0, 20, 0));

        Scene scene = new Scene(root, 480, 460);
        scene.getStylesheets().add(MainMenuScreen.class.getResource("/css/styles.css").toExternalForm());
        return scene;
    }
}
