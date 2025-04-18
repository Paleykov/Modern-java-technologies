package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) { super(message); }

    public InvalidOrderException(String message, Throwable cause) { super(message, cause); }

    public InvalidOrderException(Throwable cause) { super(cause); }
}
