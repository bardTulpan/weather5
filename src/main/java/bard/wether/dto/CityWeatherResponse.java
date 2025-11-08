package bard.wether.dto;

import java.math.BigDecimal;

public record CityWeatherResponse(String cityName, BigDecimal temperature, String description) {}