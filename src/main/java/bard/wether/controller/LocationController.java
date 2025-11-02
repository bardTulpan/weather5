package bard.wether.controller;

import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.User;
import bard.wether.service.LocationService;
import bard.wether.service.SessionService;
import bard.wether.service.WeatherService;
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
    public boolean searchLocation(@RequestParam String cityName) {
        return weatherService.isLocationExists(cityName);
    }

    @GetMapping("/showWeather")
    public Mono<WeatherResponse> showWeather(@RequestParam String cityName) {
        return weatherService.getWeather(cityName);
    }

    @PostMapping
    public void save(@CookieValue("SESSION_ID") UUID sessionId, @RequestParam String cityName) {
        User user = sessionService.validateSession(sessionId);
        if (weatherService.isLocationExists(cityName)) {
            locationService.save(cityName, user);
        }
    }

    @GetMapping
    public List<LocationWeatherDTO> getLocations(@CookieValue("SESSION_ID") UUID sessionId) {
        User user = sessionService.validateSession(sessionId);
        List<LocationWeatherDTO> locationList = locationService.getUserLocationsWithWeather(user);
        return locationList;
    }

    @DeleteMapping("/{locationId}")
    public void deleteLocation(@CookieValue("SESSION_ID") UUID sessionId, @PathVariable int locationId) {
        User user = sessionService.validateSession(sessionId);

        locationService.deleteUserLocation((long) locationId, user);
    }

}
