package pl.beda.reactive.rest.api.handler;

import io.vertx.ext.web.RoutingContext;
import pl.beda.reactive.rest.api.dto.input.InputUserDto;
import pl.beda.reactive.rest.api.service.UserService;

public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(RoutingContext rc) {
        final InputUserDto inputuserDto = rc.getBodyAsJson().mapTo(InputUserDto.class);
        userService.create(rc, inputuserDto);
    }

    public void authenticate(RoutingContext rc) {
        final InputUserDto inputuserDto = rc.getBodyAsJson().mapTo(InputUserDto.class);
        userService.validateCredentials(rc, inputuserDto);
    }

}
