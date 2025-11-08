package bard.wether.controller;

import bard.wether.dto.ErrorResponse;
import bard.wether.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemperatureConversionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleTemperatureConversion(TemperatureConversionException ex) {
        log.error("Temperature conversion error: {}", ex.getMessage(), ex);
        return new ErrorResponse("TEMPERATURE_CONVERSION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceInteractionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExternalServiceException(ExternalServiceInteractionException ex) {
        log.error("External service error: {}", ex.getMessage(), ex);
        return new ErrorResponse("EXTERNAL_SERVICE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // Изменено на 401
    public ErrorResponse handleSessionExpiredException(SessionExpiredException ex) {
        log.error("Session expired: {}", ex.getMessage(), ex);
        return new ErrorResponse("SESSION_EXPIRED", ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(AlreadyExistsException ex) {
        log.error("Resource already exists: {}", ex.getMessage(), ex);
        return new ErrorResponse("RESOURCE_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredException(InvalidCredException ex) {
        log.error("Invalid credentials: {}", ex.getMessage(), ex);
        return new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid arguments: {}", ex.getMessage(), ex);
        return new ErrorResponse("INVALID_ARGUMENTS", ex.getMessage());
    }
}