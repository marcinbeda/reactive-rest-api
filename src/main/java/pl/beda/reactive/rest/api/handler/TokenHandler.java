package pl.beda.reactive.rest.api.handler;

import io.vertx.ext.web.RoutingContext;
import pl.beda.reactive.rest.api.service.TokenService;

public class TokenHandler {

    private final TokenService tokenService;

    public TokenHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void generateToken(RoutingContext rc) {
        tokenService.create(rc);
    }

    public void validateToken(RoutingContext rc) {
        tokenService.validate(rc);
    }

}
