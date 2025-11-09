package bard.wether.dto;

import java.util.List;

public record UserLocationsResponse(List<LocationWeatherDTO> locations) {
}