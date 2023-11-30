package pl.beda.reactive.rest.api.service;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import pl.beda.reactive.rest.api.dto.input.InputUserDto;
import pl.beda.reactive.rest.api.model.User;
import pl.beda.reactive.rest.api.repository.UserRepository;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder arg2SpringSecurity;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.arg2SpringSecurity = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
    }

    public void create(RoutingContext rc, InputUserDto inputUserDto) {
        userExistsByLogin(inputUserDto.getLogin())
                .onComplete(ar -> {
                    if (!ar.result()) {
                        LOGGER.error("Login is not available.");
                        ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Login is not available.");
                    } else {
                        User user = new User();
                        user.setId(UUID.randomUUID());
                        user.setLogin(inputUserDto.getLogin());
                        user.setPassword(arg2SpringSecurity.encode(inputUserDto.getPassword()));

                        userRepository.insert(user)
                                .onSuccess(success -> {
                                    LOGGER.info("User has been created.");
                                    ResponseUtils.buildNoContentResponse(rc);
                                })
                                .onFailure(throwable -> {
                                    LOGGER.error("User creation failed.");
                                    ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Cannot create user.");
                                });
                    }
                });
    }

    private Future<Boolean> userExistsByLogin(String login) {
        return userRepository
                .findByLogin(login)
                .map(Objects::isNull);
    }

    public void validateCredentials(RoutingContext rc, InputUserDto inputuserDto) {
        userRepository
                .findByLogin(inputuserDto.getLogin())
                .onComplete(ar -> {
                    if (ar.result() == null || isPasswordValid(inputuserDto, ar)) {
                        LOGGER.error("User authentication failed.");
                        ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.UNAUTHORIZED.code(), "Wrong credentials.");
                    } else {
                        rc.next();
                    }
                });
    }

    private boolean isPasswordValid(InputUserDto inputuserDto, AsyncResult<JsonObject> ar) {
        return !arg2SpringSecurity.matches(inputuserDto.getPassword(), ar.result().getString("password"));
    }

}
