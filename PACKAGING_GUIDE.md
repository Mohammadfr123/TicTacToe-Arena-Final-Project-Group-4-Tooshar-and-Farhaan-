# Packaging Guide: Tic-Tac-Toe Arena

Since this is a JavaFX project, you cannot just run the `.class` files. You need a way to package the application so a user doesn't have to install a JRE or JavaFX separately.

##  Recommended Packaging Method: jpackage

`jpackage` (introduced in Java 14) creates a native installer (like an `.exe` on Windows) that includes its own minimized Java Runtime (JRE).

### Steps to Package:

1. **Compile the Code**:
   Compile all `.java` files into a `bin` folder.
   ```bash
   javac --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -d bin src/**/*.java
   ```

2. **Create a JAR**:
   Package the compiled classes into an executable JAR file.

3. **Run jpackage**:
   Run the following command to create an installer:
   ```bash
   jpackage --name TicTacToeArena \
            --input bin \
            --main-jar tictactoe.jar \
            --main-class TicTacToe.Main \
            --module-path "path/to/javafx/lib" \
            --add-modules javafx.controls,javafx.fxml \
            --type exe
   ```

## 🛠️ Simplified Launch for Testing
If you just want to run it in an IDE (like IntelliJ or VS Code):
1. Add the **JavaFX SDK** to your project libraries.
2. Add the **SQLite JDBC Driver** to your project libraries.
3. Add these **VM Options** to the run configuration:
   ```text
   --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml
   ```
