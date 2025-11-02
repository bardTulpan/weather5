package bard.wether.service;

import bard.wether.dto.LocationWeatherDTO;
import bard.wether.dto.WeatherResponse;
import bard.wether.entity.Location;
import bard.wether.entity.User;
import bard.wether.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final WeatherService weatherService;

    public LocationService(LocationRepository locationRepository, WeatherService weatherService) {
        this.locationRepository = locationRepository;
        this.weatherService = weatherService;
    }

    public void save(String cityName, User user) {

        WeatherResponse weatherData = weatherService.getWeather(cityName).block();

        if (weatherData == null) {
            throw new RuntimeException("Не удалось получить данные о локации");
        }

        Location location = new Location();
        location.setName(cityName);
        location.setUser(user);
        location.setLatitude(BigDecimal.valueOf(weatherData.getCoord().getLat()));
        location.setLongitude(BigDecimal.valueOf(weatherData.getCoord().getLon()));

        locationRepository.save(location);
    }

    public List<LocationWeatherDTO> getUserLocationsWithWeather(User user) {
        List<Location> locations = locationRepository.findByUser(user);


        return weatherService.getWeatherForLocations(locations);
    }

    public void deleteUserLocation(Long locationId, User user) {
        Location location = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new RuntimeException("Локация не найдена или у вас нет доступа к ней"));
        locationRepository.delete(location);
    }


}

