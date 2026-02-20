package bard.wether.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
public class PaginatedLocationsResponse {
    @JsonProperty
    private List<LocationWeatherDTO> locations;
    @JsonProperty
    private int page;
    @JsonProperty
    private int pageSize;
    @JsonProperty
    private int totalElements;
    @JsonProperty
    private int totalPages;
    @JsonProperty
    private boolean first;
    @JsonProperty
    private boolean last;
    @JsonProperty
    private boolean empty;
}

//{
//        "content": [...],           // сами данные (массив локаций)
//        "page": 0,                 // текущая страница
//        "size": 20,                // размер страницы
//        "totalElements": 150,      // всего элементов в БД
//        "totalPages": 8,           // всего страниц
//        "first": true,             // это первая страница?
//        "last": false,             // это последняя страница?
//        "empty": false             // страница пустая?
//        }
