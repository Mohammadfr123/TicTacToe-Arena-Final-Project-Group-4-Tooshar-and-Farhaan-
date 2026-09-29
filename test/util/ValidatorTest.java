package TicTacToe.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    // ---- requireNonBlankUsername: valid cases ----

    @Test
    void validUsername_ReturnsTrimmed() {
        assertEquals("Alice", assertDoesNotThrow(() -> Validator.requireNonBlankUsername("Alice")));
    }

    @Test
    void usernameWithLeadingAndTrailingWhitespace_ReturnsTrimmed() {
        assertEquals("Bob", assertDoesNotThrow(() -> Validator.requireNonBlankUsername("  Bob  ")));
    }

    @Test
    void usernameWithUnderscore_Accepted() {
        assertEquals("a_b_c", assertDoesNotThrow(() -> Validator.requireNonBlankUsername("a_b_c")));
    }

    @Test
    void usernameMixedCaseAndDigits_Accepted() {
        assertEquals("User_42", assertDoesNotThrow(() -> Validator.requireNonBlankUsername("User_42")));
    }

    // ---- requireNonBlankUsername: failure cases ----

    @Test
    void nullUsername_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername(null),
                "Username can't be empty.");
    }

    @Test
    void emptyUsername_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername(""),
                "Username can't be empty.");
    }

    @Test
    void blankUsername_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername("   "),
                "Username can't be empty.");
    }

    @Test
    void usernameAtMaxLength_Accepted() {
        String atMax = "a".repeat(20);
        assertEquals(atMax, assertDoesNotThrow(() -> Validator.requireNonBlankUsername(atMax)));
    }

    @Test
    void usernameOverMaxLength_ThrowsValidationException() {
        String over = "a".repeat(21);
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername(over),
                "Username must be 20 characters or fewer.");
    }

    @Test
    void usernameWithInvalidCharacter_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername("bad-name"),
                "Username can only contain letters, numbers, and underscores.");
    }

    @Test
    void usernameWithSpace_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankUsername("two words"),
                "Username can only contain letters, numbers, and underscores.");
    }

    // ---- requireNonBlankTournamentName: valid cases ----

    @Test
    void validTournamentName_ReturnsTrimmed() {
        assertEquals("Summer Cup", assertDoesNotThrow(() -> Validator.requireNonBlankTournamentName("Summer Cup")));
    }

    @Test
    void tournamentNameWithWhitespace_ReturnsTrimmed() {
        assertEquals("ALCHE Cup", assertDoesNotThrow(() -> Validator.requireNonBlankTournamentName("  ALCHE Cup  ")));
    }

    // ---- requireNonBlankTournamentName: failure cases ----

    @Test
    void nullTournamentName_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankTournamentName(null),
                "Tournament name can't be empty.");
    }

    @Test
    void emptyTournamentName_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankTournamentName(""),
                "Tournament name can't be empty.");
    }

    @Test
    void blankTournamentName_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.requireNonBlankTournamentName("   "),
                "Tournament name can't be empty.");
    }
}
