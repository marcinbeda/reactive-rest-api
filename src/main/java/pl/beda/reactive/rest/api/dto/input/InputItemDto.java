package pl.beda.reactive.rest.api.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InputItemDto implements Serializable {

    @JsonProperty(value = "title")
    private String title;

}
