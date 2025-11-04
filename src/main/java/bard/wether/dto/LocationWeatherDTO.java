package bard.wether.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationWeatherDTO {
    private Long id;
    private String name;
    private String temperature;
}
