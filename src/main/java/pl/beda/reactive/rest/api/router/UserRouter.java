package pl.beda.reactive.rest.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import pl.beda.reactive.rest.api.handler.TokenHandler;
import pl.beda.reactive.rest.api.handler.UserHandler;
import pl.beda.reactive.rest.api.handler.UserValidationHandler;

public class UserRouter {

    private final Vertx vertx;
    private final UserHandler userHandler;
    private final TokenHandler tokenHandler;
    private final UserValidationHandler userValidationHandler;

    public UserRouter(Vertx vertx, UserHandler userHandler, TokenHandler tokenHandler, UserValidationHandler userValidationHandler) {
        this.vertx = vertx;
        this.userHandler = userHandler;
        this.tokenHandler = tokenHandler;
        this.userValidationHandler = userValidationHandler;
    }

    public void buildUserRouter(Router router) {
        router.route("/*").handler(BodyHandler.create());
        router.post("/register")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(userValidationHandler.register())
                .handler(userHandler::register);
        router.post("/login")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(userValidationHandler.login())
                .handler(userHandler::authenticate)
                .handler(tokenHandler::generateToken);
    }

}
