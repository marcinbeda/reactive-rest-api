package pl.beda.reactive.rest.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import pl.beda.reactive.rest.api.handler.*;
import pl.beda.reactive.rest.api.repository.ItemRepository;
import pl.beda.reactive.rest.api.repository.UserRepository;
import pl.beda.reactive.rest.api.router.ItemRouter;
import pl.beda.reactive.rest.api.router.UserRouter;
import pl.beda.reactive.rest.api.service.ItemService;
import pl.beda.reactive.rest.api.service.TokenService;
import pl.beda.reactive.rest.api.service.UserService;
import pl.beda.reactive.rest.api.utils.ConfigUtils;
import pl.beda.reactive.rest.api.utils.DbUtils;
import pl.beda.reactive.rest.api.utils.JwtUtils;
import pl.beda.reactive.rest.api.utils.LogUtils;

public class ApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);
    private static final int DEFAULT_PORT = 8080;

    @Override
    public void start(Promise<Void> promise) {
        final MongoClient dbClient = DbUtils.buildDbClient(vertx);
        final JWTAuth jwtAuth = JWTAuth.create(vertx, JwtUtils.getConfiguration());

        final UserRepository userRepository = new UserRepository(dbClient);
        final ItemRepository itemRepository = new ItemRepository(dbClient);

        final TokenService tokenService = new TokenService(jwtAuth, userRepository);
        final UserService userService = new UserService(userRepository);
        final ItemService itemService = new ItemService(tokenService, itemRepository);

        final UserHandler userHandler = new UserHandler(userService);
        final ItemHandler itemHandler = new ItemHandler(itemService);
        final TokenHandler tokenHandler = new TokenHandler(tokenService);

        final UserValidationHandler userValidationHandler = new UserValidationHandler(vertx);
        final ItemValidationHandler itemValidationHandler = new ItemValidationHandler(vertx);

        final UserRouter userRouter = new UserRouter(userHandler, tokenHandler, userValidationHandler);
        final ItemRouter itemRouter = new ItemRouter(itemHandler, tokenHandler, itemValidationHandler);

        final Router router = Router.router(vertx);
        ErrorHandler.buildHandler(router);
        userRouter.buildUserRouter(router);
        itemRouter.buildItemRouter(router);

        buildHttpServer(vertx, promise, router);
    }

    private void buildHttpServer(Vertx vertx, Promise<Void> promise, Router router) {
        String portProperty = ConfigUtils.getInstance().getProperties().getProperty("server.port");
        final int port = portProperty == null || portProperty.isBlank() ? DEFAULT_PORT : Integer.parseInt(portProperty);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        promise.complete();
                        LOGGER.info(LogUtils.RUN_HTTP_SERVER_SUCCESS_MESSAGE.buildMessage(port));
                    } else {
                        promise.fail(http.cause());
                        LOGGER.info(LogUtils.RUN_HTTP_SERVER_ERROR_MESSAGE.buildMessage());
                    }
                });
    }

}
