package bard.wether.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleWeatherDTO {
    private String city;
    private String description;
}
