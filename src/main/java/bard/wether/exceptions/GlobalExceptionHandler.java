package bard.wether.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //добавить dto ошибки status message


    @ExceptionHandler(TemperatureConversionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleTemperatureConversion(TemperatureConversionException ex) {
        System.out.println(ex.getMessage() + " " + ex.getCause());
        return "TEMPERATURE_CONVERSION_ERROR";
    }

    @ExceptionHandler(OpenWeatherException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOpenWeatherException(OpenWeatherException ex) {
        System.out.println("Open Weather exception: " + ex.getMessage());
        return "OPEN WEATHER ERROR";
    }


}



