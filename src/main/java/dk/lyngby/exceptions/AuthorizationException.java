package dk.lyngby.exceptions;

public class AuthorizationException extends Exception {
    private final int statusCode;

    public AuthorizationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
