package bard.wether.service;

import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.SimpleWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.Location;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.ExternalServiceInteractionException;
import bard.wether.exceptions.TemperatureConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {


    private final WebClient webClient;

    private String apiKey;

    private String apiUrl;

    public WeatherService(WebClient.Builder webClientBuilder, @Value("${weather.api.key}") String apiKey, @Value("${weather.api.url}") String apiUrl) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .build();
    }

    public Mono<Boolean> isLocationExists(String cityName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", cityName)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new NotFoundException("City not found"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ExternalServiceInteractionException("Error Open Weather API"))
                )
                .bodyToMono(WeatherResponse.class)
                .map(response -> true)
                .onErrorReturn(false);

    }

    public String getTemperatureForCity(String cityName) {
        WeatherResponse weatherResponse = getWeather(cityName)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException(cityName + "not found"));

        if (weatherResponse.getMain() == null) {
            throw new ExternalServiceInteractionException("No temperature data for city: " + cityName);
        }

        double tempKelvin = weatherResponse.getMain().getTemp();
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


    public Mono<WeatherResponse> getWeather(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new NotFoundException("Город '" + city + "' не найден"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ExternalServiceInteractionException("Ошибка сервера OpenWeather"))
                )
                .bodyToMono(WeatherResponse.class);
    }

    public Mono<SimpleWeatherDTO> getSimpleWeather(String city) {
        return getWeather(city)
                .map(response -> {
                    String cityName = response.getName();
                    String description = response.getWeather() != null && !response.getWeather().isEmpty() ? response.getWeather().get(0).getDescription() : "no data";
                    return new SimpleWeatherDTO(cityName, description);
                });
    }
}


