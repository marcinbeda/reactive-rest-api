package pl.beda.reactive.rest.api.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.ext.web.validation.BodyProcessorException;
import io.vertx.ext.web.validation.ParameterProcessorException;
import io.vertx.ext.web.validation.RequestPredicateException;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

public class ErrorHandler {

    public static void buildHandler(Router router) {
        router.errorHandler(400, rc -> {
            if (rc.failure() instanceof BadRequestException) {
                if (rc.failure() instanceof ParameterProcessorException) {
                    ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Path parameter is invalid");
                } else if (rc.failure() instanceof BodyProcessorException | rc.failure() instanceof RequestPredicateException) {
                    ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Request body is invalid");
                }
            }
        });
    }

}
