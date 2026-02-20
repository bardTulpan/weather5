package bard.wether.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class Coordinates {
    @JsonProperty("lon")
    private BigDecimal latitude;
    @JsonProperty("lat")
    private BigDecimal longitude;

}