package pl.beda.reactive.rest.api.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Item {

    private UUID id;
    private UUID owner;
    private String name;

}
