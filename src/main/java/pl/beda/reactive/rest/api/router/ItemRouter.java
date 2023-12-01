package pl.beda.reactive.rest.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import pl.beda.reactive.rest.api.handler.ItemHandler;
import pl.beda.reactive.rest.api.handler.ItemValidationHandler;
import pl.beda.reactive.rest.api.handler.TokenHandler;

public class ItemRouter {

    private final Vertx vertx;
    private final ItemHandler itemHandler;
    private final TokenHandler tokenHandler;
    private final ItemValidationHandler itemValidationHandler;

    public ItemRouter(Vertx vertx, ItemHandler itemHandler, TokenHandler tokenHandler, ItemValidationHandler itemValidationHandler) {
        this.vertx = vertx;
        this.itemHandler = itemHandler;
        this.tokenHandler = tokenHandler;
        this.itemValidationHandler = itemValidationHandler;
    }

    public void buildItemRouter(Router router) {
        router.route("/*").handler(BodyHandler.create());
        router.post("/items")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(itemValidationHandler.addItem())
                .handler(tokenHandler::validateToken)
                .handler(itemHandler::addItem);
        router.get("/items")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(tokenHandler::validateToken)
                .handler(itemHandler::readAllItems);
    }

}
