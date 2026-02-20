package bard.wether.dto;

import bard.wether.model.Coordinates;
import bard.wether.model.WeatherCondition;
import bard.wether.model.WeatherMain;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class WeatherResponse {

    @JsonProperty("coord")
    private Coordinates coordinates;

    @JsonProperty("main")
    private WeatherMain weatherMain;

    @JsonProperty("weather")
    private List<WeatherCondition> weatherConditions;

    @JsonProperty("name")
    private String cityName;

}