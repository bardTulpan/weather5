package bard.wether.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WeatherCondition {

    @JsonProperty("main")
    private String main;

    @JsonProperty("description")
    private String description;

    @JsonProperty("icon")
    private String icon;

}
