package bard.wether.service;

import bard.wether.client.WeatherClient;
import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.Location;
import bard.wether.exceptions.ExternalServiceInteractionException;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.TemperatureConversionException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public String getTemperatureForCity(String cityName) {
        WeatherResponse weatherResponse = weatherClient.getWeatherData(cityName)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException(cityName + "not found"));

        if (weatherResponse.getWeatherMain() == null) {
            throw new ExternalServiceInteractionException("No temperature data for city: " + cityName);
        }

        double tempKelvin = weatherResponse.getWeatherMain().getTemperature();
        if (tempKelvin < 0) {
            throw new TemperatureConversionException("Invalid temperature value: " + tempKelvin);
        }

        double tempCelsius = tempKelvin - 273.15;
        return String.format("%.1f°C", tempCelsius);

    }

    public List<LocationWeatherDTO> getWeatherForLocations(List<Location> locations) {
        List<LocationWeatherDTO> result = new ArrayList<>();

        for (Location location : locations) {
            String temperature = getTemperatureForCity(location.getName());
            LocationWeatherDTO dto = new LocationWeatherDTO(
                    location.getId(),
                    location.getName(),
                    temperature
            );
            result.add(dto);
        }

        return result;
    }
}


