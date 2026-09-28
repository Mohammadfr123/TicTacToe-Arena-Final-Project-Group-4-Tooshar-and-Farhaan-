# Tic-Tac-Toe Arena

A professional Tic-Tac-Toe implementation featuring a permanent match history, tournament tracking, and player profiles.

##  Features
- **Two-Player Mode**: Classic head-to-head gameplay.
- **Player Management**: Full CRUD for player profiles.
- **Tournament System**: Create and track tournaments.
- **Persistent History**: All matches and moves are stored in an SQL database via JDBC.
- **Recursive Win Detection**: Structural win-checking algorithm.

## Build & Launch

### Prerequisites
- Java 17 or higher (JDK)
- JavaFX SDK

### Running the Application
1. Clone the repository.
2. Compile the Java files.
3. Run the main entry point (ensure the `/css/styles.css` is in the classpath).

### Database Configuration
The application is designed to be "zero-config". On the first execution, the app automatically creates the necessary SQL schema (Tables for Players, Tournaments, Rounds, Matches, and Moves) using JDBC DDL statements.

## Project Structure
- `UserInterface/`: JavaFX screens and navigation logic.
- `util/`: Input validation and utility dialogs.
- `model/`: Domain entities.
- `dao/`: Data Access Objects for SQL operations.


## How to compile and run on linux (TKS's laptop ,nobara)
- Compile: 
javac --module-path "/home/tks/Downloads/javafx-sdk-26.0.2/lib" \
--add-modules ALL-MODULE-PATH \
-cp "lib/sqlite-jdbc-3.46.1.3.jar" \
-d out \
$(find . -name "*.java")
./dao/MatchDAO.java:30: error: <identifier> expected
stmt.setNull(1, java.sql.Types.INTEGER.);
^
./dao/MatchDAO.java:43: error: <identifier> expected
stmt.setNull(4, java.sql.Types.INTEGER.);
^
2 errors

- Run:
