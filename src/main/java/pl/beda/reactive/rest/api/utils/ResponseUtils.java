package pl.beda.reactive.rest.api.utils;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ResponseUtils {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    public static void buildOkResponse(RoutingContext rc, Object response) {
        rc.response()
                .setStatusCode(200)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
    }

    public static void buildNoContentResponse(RoutingContext rc) {
        rc.response()
                .setStatusCode(204)
                .end();
    }

    public static void buildErrorResponse(RoutingContext rc, final int status, final String message) {
        rc.response()
                .setStatusCode(status)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .end(new JsonObject().put("error", message).encodePrettily());
    }

}
