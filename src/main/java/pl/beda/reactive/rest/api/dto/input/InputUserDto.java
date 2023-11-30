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
public class InputUserDto implements Serializable {

    @JsonProperty(value = "login")
    private String login;

    @JsonProperty(value = "password")
    private String password;

}
