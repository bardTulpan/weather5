package bard.wether.exceptions;

import bard.wether.dto.ApiResponse;
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
    public ApiResponse<String> handleTemperatureConversion(TemperatureConversionException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @ExceptionHandler(OpenWeatherException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleOpenWeatherException(OpenWeatherException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleSessionExpiredException(SessionExpiredException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<String> handleAlreadyExistsException(AlreadyExistsException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage(),  HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(InvalidCredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleInvalidCredException(InvalidCredException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage(),  HttpStatus.UNAUTHORIZED.value());
    }



}



