package bard.wether.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WeatherMain {
    @JsonProperty("temp")
    private double temperature;
    @JsonProperty("humidity")
    private int humidity;
    @JsonProperty("feels_like")
    private double feelsLike;
    @JsonProperty("pressure")
    private double pressure;

}