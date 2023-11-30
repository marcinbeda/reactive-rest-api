package pl.beda.reactive.rest.api.service;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import pl.beda.reactive.rest.api.dto.input.InputUserDto;
import pl.beda.reactive.rest.api.dto.output.OutputTokenDto;
import pl.beda.reactive.rest.api.repository.UserRepository;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private final UserRepository userRepository;
    private final JWTAuth jwtAuth;

    public TokenService(JWTAuth jwtAuth, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtAuth = jwtAuth;
    }

    public void create(RoutingContext rc) {
        final InputUserDto inputuserDto = rc.getBodyAsJson().mapTo(InputUserDto.class);
        userRepository
                .findByLogin(inputuserDto.getLogin())
                .onComplete(ar -> {
                    if (ar.result() == null) {
                        LOGGER.error("Token creation failed.");
                        ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Token creation failed.");
                    } else {
                        OutputTokenDto outputTokenDto = new OutputTokenDto(jwtAuth.generateToken(new JsonObject().put("UUID", ar.result().getString("id"))));
                        LOGGER.info("Token has been created.");
                        ResponseUtils.buildOkResponse(rc, outputTokenDto);
                    }
                });
    }

    public void validate(RoutingContext rc) {
        extractTokenFromAuthorizationHeader(rc)
                .onSuccess(token -> jwtAuth.authenticate(new JsonObject().put("token", token))
                        .onSuccess(success -> rc.next())
                        .onFailure(err -> {
                            LOGGER.error("Token validation failed.");
                            ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.UNAUTHORIZED.code(), "Wrong token.");
                        }));
    }

    private Future<String> extractTokenFromAuthorizationHeader(RoutingContext rc) {
        Promise<String> promise = Promise.promise();
        String authorizationHeader = rc.request().headers().get(HttpHeaderNames.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            LOGGER.error("Missing authorization header.");
            ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.UNAUTHORIZED.code(), "Missing token.");
            promise.fail("Missing authorization header.");
        } else {
            promise.complete(authorizationHeader.substring(("Bearer").length()).trim());
        }
        return promise.future();
    }

    public Future<User> getUserFromToken(RoutingContext rc) {
        Promise<User> promise = Promise.promise();
        extractTokenFromAuthorizationHeader(rc)
                .onSuccess(token -> jwtAuth.authenticate(new JsonObject().put("token", token))
                        .onFailure(err -> {
                            LOGGER.error("Token validation failed.");
                            ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.UNAUTHORIZED.code(), "Wrong token.");
                            promise.fail("Token validation failed.");
                        })
                        .onSuccess(promise::complete));
        return promise.future();
    }

}
