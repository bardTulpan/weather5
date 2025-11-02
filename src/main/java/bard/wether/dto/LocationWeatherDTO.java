package bard.wether.dto;

public class LocationWeatherDTO {
    private Long id;
    private String name;
    private String temperature;

    // Конструкторы
    public LocationWeatherDTO() {
    }

    public LocationWeatherDTO(Long id, String name, String temperature) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "LocationWeatherDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", temperature='" + temperature + '\'' +
                '}';
    }
}