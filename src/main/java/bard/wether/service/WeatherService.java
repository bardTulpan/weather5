package bard.wether.service;

import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.SimpleWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.Location;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.OpenWeatherException;
import bard.wether.exceptions.TemperatureConversionException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {


    private final WebClient webClient;
    private final String API_KEY = "fc1e39b85ff6b83957c733adb0e65cf8";

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .build();
    }

    public boolean isLocationExists(String cityName) {
        WeatherResponse weatherResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", cityName)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new NotFoundException("City not found"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new OpenWeatherException("Error Open Weather API"))
                )
                .bodyToMono(WeatherResponse.class)
                .block();

        return weatherResponse != null;
    }

    public String getTemperatureForCity(String cityName) {
        WeatherResponse weatherResponse = getWeather(cityName)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException(cityName + "not found"));

        if (weatherResponse.getMain() == null) {
            throw new OpenWeatherException("No temperature data for city: " + cityName);
        }

        // Конвертируем из Кельвинов в Цельсии
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


    public Mono<WeatherResponse> getWeather(String city) { //смута пипец обработка такая-себе
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new NotFoundException("Город '" + city + "' не найден"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new OpenWeatherException("Ошибка сервера OpenWeather"))
                )
                .bodyToMono(WeatherResponse.class);
        // УБИРАЕМ onErrorResume - пусть исключения прокидываются
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


