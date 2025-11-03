package bard.wether.controller;

import bard.wether.dto.ApiResponse;
import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.User;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.OpenWeatherException;
import bard.wether.service.LocationService;
import bard.wether.service.SessionService;
import bard.wether.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/locations")
@RestController
public class LocationController {


    private final WeatherService weatherService;
    private final LocationService locationService;
    private final SessionService sessionService;

    public LocationController(WeatherService weatherService, LocationService locationService, SessionService sessionService) {
        this.weatherService = weatherService;
        this.locationService = locationService;
        this.sessionService = sessionService;
    }

    @GetMapping("/search")
    public ApiResponse<Mono<Boolean>> searchLocation(@RequestParam String cityName) {
        if (weatherService.isLocationExists(cityName).block()) {
            return ApiResponse.success("City exists", null);
        }
        throw new NotFoundException("City not found"); //наверное можно написать без этого
    }

    @GetMapping("/showWeather")
    public ApiResponse<WeatherResponse> showWeather(@RequestParam String cityName) {
        return ApiResponse.success(weatherService.getWeather(cityName).block());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> save(@CookieValue("SESSION_ID") UUID sessionId, @RequestParam String cityName) {
        User user = sessionService.validateSession(sessionId);
        if (weatherService.isLocationExists(cityName).block()) {
            locationService.save(cityName, user);
            return ApiResponse.success("Location saved", null);
        }
        throw new NotFoundException("Location not found");
    }

    @GetMapping
    public ApiResponse<List<LocationWeatherDTO>> getLocations(@CookieValue("SESSION_ID") UUID sessionId) {
        User user = sessionService.validateSession(sessionId);
        List<LocationWeatherDTO> locationList = locationService.getUserLocationsWithWeather(user);
        return ApiResponse.success("User locations retrieved", locationList);
    }

    @DeleteMapping("/{locationId}")
    public ApiResponse<Void> deleteLocation(@CookieValue("SESSION_ID") UUID sessionId, @PathVariable int locationId) {
        User user = sessionService.validateSession(sessionId);
        locationService.deleteUserLocation((long) locationId, user);
        return ApiResponse.success("Location deleted", null);
    }

}
