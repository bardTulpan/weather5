package bard.wether.controller;

import bard.wether.client.WeatherClient;
import bard.wether.dto.*;
import bard.wether.entity.User;
import bard.wether.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/locations")
@RestController
public class LocationController {


    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    private User getCurrentUser(HttpServletRequest request) {
        return (User) request.getAttribute("CURRENT_USER");
    }

    @GetMapping("/search/validate")
    public CityValidationResponse validateCity(@RequestParam String cityName) {
        return locationService.validateCity(cityName);
    }

    @GetMapping("/weather")
    public CityWeatherResponse getCityWeather(@RequestParam String cityName) {
        return locationService.getCityWeather(cityName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationCreatedResponse save(@RequestParam String cityName, HttpServletRequest request) {
        User user = getCurrentUser(request);
        return locationService.createLocation(cityName, user);
    }

    @GetMapping
    public UserLocationsResponse getUserLocations(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return locationService.getUserLocations(user);
    }

    @GetMapping(value = "/paginated")
    public PaginatedLocationsResponse getLocationsWithPag(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, HttpServletRequest httpServletRequest) {
        User user = getCurrentUser(httpServletRequest);
        return locationService.getPaginatedLocations(user, page, size);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable long locationId, HttpServletRequest request) {
        User user = getCurrentUser(request);
        locationService.deleteUserLocation(locationId, user);
    }

}
