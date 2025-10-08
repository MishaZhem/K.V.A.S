package kvas.uberchallenge.exception;

public class MLModelException extends RuntimeException {
    public MLModelException(String message) {
        super(message);
    }

    public MLModelException(String message, Throwable cause) {
        super(message, cause);
    }
}

