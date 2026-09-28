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
