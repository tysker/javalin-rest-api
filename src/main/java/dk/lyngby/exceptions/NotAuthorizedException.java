package dk.lyngby.exceptions;

public class NotAuthorizedException extends Exception {
    private final int statusCode;
    private Object object;

    public NotAuthorizedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public NotAuthorizedException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getObject() {
        return object;
    }
}
