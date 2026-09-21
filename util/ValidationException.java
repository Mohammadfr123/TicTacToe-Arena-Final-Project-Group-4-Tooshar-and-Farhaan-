package TicTacToe.util;

/*For bad input to never be shown as a console stack trace.*/

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
