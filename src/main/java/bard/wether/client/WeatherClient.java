package bard.wether.client;

import bard.wether.dto.WeatherResponse;
import bard.wether.exceptions.ExternalServiceInteractionException;
import bard.wether.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WeatherClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;

    public WeatherClient(WebClient.Builder webClientBuilder,
                         @Value("${weather.api.key}") String apiKey,
                         @Value("${weather.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
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

    public Mono<WeatherResponse> getWeatherData(String city) {
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
}


