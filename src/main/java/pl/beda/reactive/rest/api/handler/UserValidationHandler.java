package pl.beda.reactive.rest.api.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.draft7.dsl.StringFormat;

import java.util.regex.Pattern;

import static io.vertx.json.schema.common.dsl.Keywords.*;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;
import static io.vertx.json.schema.draft7.dsl.Keywords.format;

public class UserValidationHandler {

    private final Vertx vertx;

    public UserValidationHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public ValidationHandler register() {
        final SchemaParser schemaParser = buildSchemaParser();
        final ObjectSchemaBuilder schemaBuilder = buildRegisterBodySchemaBuilder();
        return ValidationHandler
                .builder(schemaParser)
                .predicate(RequestPredicate. BODY_REQUIRED)
                .body(Bodies.json(schemaBuilder))
                .build();
    }

    public ValidationHandler login() {
        final SchemaParser schemaParser = buildSchemaParser();
        final ObjectSchemaBuilder schemaBuilder = buildLoginBodySchemaBuilder();
        return ValidationHandler
                .builder(schemaParser)
                .predicate(RequestPredicate.BODY_REQUIRED)
                .body(Bodies.json(schemaBuilder))
                .build();
    }

    private SchemaParser buildSchemaParser() {
        return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(vertx, new SchemaRouterOptions()));
    }

    private ObjectSchemaBuilder buildRegisterBodySchemaBuilder() {
        return objectSchema()
                .requiredProperty("login", stringSchema().with(format(StringFormat.EMAIL)).with(maxLength(255)))
                .requiredProperty("password", stringSchema().with(pattern(Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,64}$"))));
    }

    private ObjectSchemaBuilder buildLoginBodySchemaBuilder() {
        return objectSchema()
                .requiredProperty("login", stringSchema().with(format(StringFormat.EMAIL)).with(maxLength(255)))
                .requiredProperty("password", stringSchema().with(minLength(1)));
    }

}
