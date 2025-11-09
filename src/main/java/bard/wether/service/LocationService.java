package bard.wether.service;

import bard.wether.client.WeatherClient;
import bard.wether.dto.*;
import bard.wether.entity.Location;
import bard.wether.entity.User;
import bard.wether.exceptions.ExternalServiceInteractionException;
import bard.wether.exceptions.NotFoundException;
import bard.wether.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final WeatherClient weatherClient;
    private final WeatherService weatherService;

    public LocationService(LocationRepository locationRepository, WeatherService weatherService, WeatherClient weatherClient) {
        this.locationRepository = locationRepository;
        this.weatherService = weatherService;
        this.weatherClient = weatherClient;
    }

    public CityValidationResponse validateCity(String cityName) {
        boolean exists = weatherClient.isLocationExists(cityName).block();
        return new CityValidationResponse(exists, exists ? "City is valid" : "City not found");
    }

    public CityWeatherResponse getCityWeather(String cityName) {
        WeatherResponse weatherData = weatherClient.getWeatherData(cityName).block();
        if (weatherData == null) {
            throw new ExternalServiceInteractionException("Failed to get weather data for city: " + cityName);
        }

        BigDecimal temperature = convertKelvinToCelsius(BigDecimal.valueOf(weatherData.getWeatherMain().getTemperature()));
        String description = weatherData.getWeatherConditions().isEmpty() ?
                "No description" : weatherData.getWeatherConditions().get(0).getDescription();

        return new CityWeatherResponse(cityName, temperature, description);
    }

    public LocationCreatedResponse createLocation(String cityName, User user) {
        CityValidationResponse validation = validateCity(cityName);
        if (!validation.isValid()) {
            throw new NotFoundException("City '" + cityName + "' not found");
        }

        WeatherResponse weatherData = weatherClient.getWeatherData(cityName).block();
        if (weatherData == null) {
            throw new ExternalServiceInteractionException("Failed to get location data");
        }

        Location location = new Location(
                cityName,
                user,
                weatherData.getCoordinates().getLatitude(),
                weatherData.getCoordinates().getLongitude()
        );

        Location savedLocation = locationRepository.save(location);
        return new LocationCreatedResponse(savedLocation.getId(), savedLocation.getName());
    }

    public UserLocationsResponse getUserLocations(User user) {
        List<Location> locations = locationRepository.findByUser(user);
        List<LocationWeatherDTO> locationDTOs = weatherService.getWeatherForLocations(locations);
        return new UserLocationsResponse(locationDTOs);
    }

    public PaginatedLocationsResponse getPaginatedLocations(User user, int page, int size) {
        validatePaginationParams(page, size);

        int offset = page * size;
        List<Location> locations = locationRepository.findByUserWithPagination(user, size, offset);
        long totalElements = locationRepository.countByUser(user);

        List<LocationWeatherDTO> pageContent = weatherService.getWeatherForLocations(locations);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isFirst = page == 0;
        boolean isLast = page >= totalPages - 1;
        boolean isEmpty = pageContent.isEmpty();

        return new PaginatedLocationsResponse(pageContent, page, size, (int) totalElements, totalPages, isFirst, isLast, isEmpty);
    }

    public void deleteUserLocation(Long locationId, User user) {
        Location location = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new NotFoundException("Location not found or no access"));
        locationRepository.delete(location);
    }

    private BigDecimal convertKelvinToCelsius(BigDecimal kelvin) {
        return kelvin.subtract(new BigDecimal("273.15")).setScale(1, RoundingMode.HALF_UP);
    }

    private void validatePaginationParams(int page, int pageSize) {
        if (page < 0) throw new IllegalArgumentException("Page cannot be negative");
        if (pageSize < 1 || pageSize > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
    }
}
