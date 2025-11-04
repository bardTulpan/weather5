package bard.wether.exceptions;


public class ExternalServiceInteractionException extends RuntimeException {
    public ExternalServiceInteractionException(String message) {
        super(message);
    }
}