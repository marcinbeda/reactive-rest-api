package pl.beda.reactive.rest.api.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

import static io.vertx.json.schema.common.dsl.Keywords.maxLength;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;

public class ItemValidationHandler {

    private final Vertx vertx;

    public ItemValidationHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public ValidationHandler addItem() {
        final SchemaParser schemaParser = buildSchemaParser();
        final ObjectSchemaBuilder schemaBuilder = buildAddItemBodySchemaBuilder();
        return ValidationHandler
                .builder(schemaParser)
                .predicate(RequestPredicate.BODY_REQUIRED)
                .body(Bodies.json(schemaBuilder))
                .build();
    }

    private SchemaParser buildSchemaParser() {
        return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(vertx, new SchemaRouterOptions()));
    }

    private ObjectSchemaBuilder buildAddItemBodySchemaBuilder() {
        return objectSchema()
                .requiredProperty("title", stringSchema().with(maxLength(255)));
    }

}
