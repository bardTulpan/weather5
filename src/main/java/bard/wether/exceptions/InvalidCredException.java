package bard.wether.exceptions;

public class InvalidCredException extends RuntimeException {
    public InvalidCredException(String message) {
        super(message);
    }
}