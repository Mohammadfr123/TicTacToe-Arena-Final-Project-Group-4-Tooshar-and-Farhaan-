package TicTacToe.util;

/*All input validation lives here*/

public class Validator {

    private static final int MAX_USERNAME_LENGTH = 20;

    /* ValidationException with a human-readable reason if the username is invalid */
    public static String requireNonBlankUsername(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Username can't be empty.");
        }

        String trimmed = input.trim();
        if (trimmed.length() > MAX_USERNAME_LENGTH) {
            throw new ValidationException("Username must be " + MAX_USERNAME_LENGTH + " characters or fewer.");
        }
        if (!trimmed.matches("[A-Za-z0-9_]+")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores.");
        }
        return trimmed;
    }

    /* ValidationException with a human-readable reason if the tournament name is invalid */

    public static String requireNonBlankTournamentName(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Tournament name can't be empty.");
        }
        return input.trim();
    }
}
