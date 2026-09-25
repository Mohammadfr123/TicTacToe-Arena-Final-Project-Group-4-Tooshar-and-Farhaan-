# Tic-Tac-Toe Arena - Viva & Project Guide

This document is designed to help us explain the project during our Viva. It covers the architecture, the logic, and how the components interact.

## 1. Overall Architecture
The project follows a **Layered Architecture**:
- **GUI Layer (JavaFX)**: Handles user interaction and displays data.
- **Service/Controller Layer (`SceneManager`)**: Manages navigation between screens and coordinates data flow.
- **Data Access Layer (DAO Pattern)**: Isolates the SQL database logic from the rest of the app.
- **Domain Model**: Simple Java objects (`Player`, `Tournament`) representing the entities.

---

## 2. Farhaan's Contribution (GUI & UX)
### The Screens
- **Main Menu**: The entry point. It uses a `SceneManager` to switch views.
- **Player Profiles**: Implements **Full CRUD**. You can Create, Read, Update, and Delete players.
- **Tournament Screen**: Allows creating and viewing tournaments.
- **Game Board**: A grid of buttons. It handles turns and triggers the win-checker.

### Key Technical Implementation
- **Input Validation**: Uses a dedicated `Validator` class. If a user enters a blank name, a `ValidationException` is thrown and caught by the GUI, which shows a clean `Alert` instead of a crash (stack trace).
- **Navigation**: The `SceneManager` holds the `Stage` and the `DAOs`, ensuring that only one instance of the database connection logic is shared across screens.

---

## 3. Tooshar's Contribution (Logic & Database)
### The Database (DAO Pattern)
Tooshar implemented the **Data Access Object (DAO)** pattern. 
- **Why?** To ensure that the GUI code doesn't contain any SQL queries. If we change from SQLite to MySQL, we only change the DAO classes, not the GUI.
- **JDBC**: Uses `PreparedStatement` to prevent **SQL Injection** and `try-with-resources` to ensure database connections are closed automatically.

### The Recursion (Win Detection)
Instead of using a long list of `if` statements, the project uses a **Recursive Algorithm** to check for winners.
- **How it works**: 
  1. The app defines 8 winning lines (3 rows, 3 cols, 2 diags).
  2. A recursive function walks through the list of lines.
  3. For each line, another recursive function checks the 3 cells.
- **Base Cases**: 
  - If a cell doesn't match the player $\rightarrow$ Line fails.
  - If we reach the end of the 3-cell list $\rightarrow$ Line succeeds (Win!).
  - If we check all 8 lines and none succeed $\rightarrow$ No winner yet.

---

## 4. Possible Viva Questions & Answers

**Q: Why use the DAO pattern?**
**A:** It separates the business logic from the data storage. It makes the code more maintainable and testable.

**Q: Why use recursion for win-checking instead of a loop?**
**A:** It's more structural. The problem of checking lines is naturally a tree of sub-problems. Recursion allows us to handle different board sizes (e.g., 4x4) by simply changing the data, without rewriting the logic.

**Q: How do you handle database errors in the GUI?**
**A:** We use a custom `ValidationException` and a `DialogUtil` class to show user-friendly error messages in a JavaFX Alert window.

**Q: What is the time complexity of the win check?**
**A:** Since the board is always 3x3, the complexity is $O(1)$ (constant time), as it checks a maximum of 8 lines of 3 cells each.
