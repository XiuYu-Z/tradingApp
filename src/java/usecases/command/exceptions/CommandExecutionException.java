package usecases.command.exceptions;

/**
 * Created at 8.3
 */

public class CommandExecutionException extends Exception {

    /**
     * Instance variable
     */
    Exception exception;

    /**
     * Instantiates an instance of this class.
     *
     * @param e The underlying exception which this class wraps
     */
    public CommandExecutionException(Exception e) {
        this.exception = e;
    }

    /**
     * Returns the underlying exception.
     *
     * @return The underlying exception
     */
    public Exception getException() {
        return exception;
    }


}
