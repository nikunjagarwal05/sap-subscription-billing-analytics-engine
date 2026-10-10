package projectsap.sap.common.exception;

/**
 * Custom exception representing errors encountered during JSON parsing or POJO conversion.
 */
public class SapParseException extends Exception {

    public SapParseException(String message) {
        super(message);
    }

    public SapParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
