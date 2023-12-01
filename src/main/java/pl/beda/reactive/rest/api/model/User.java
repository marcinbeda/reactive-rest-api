package pl.beda.reactive.rest.api.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    private UUID id;
    private String login;
    private String password;

}
