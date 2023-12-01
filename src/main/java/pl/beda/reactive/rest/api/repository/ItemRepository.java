package pl.beda.reactive.rest.api.repository;

import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import pl.beda.reactive.rest.api.model.Item;

import java.util.List;

public class ItemRepository {

    private final MongoClient dbClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRepository.class);
    private static final String ITEMS_COLLECTION_NAME = "items";

    public ItemRepository(MongoClient dbClient) {
        this.dbClient = dbClient;
    }

    public Future<String> insert(Item item) {
        return dbClient.save(ITEMS_COLLECTION_NAME, new JsonObject(Json.encode(item)))
                .onSuccess(success -> LOGGER.info("Item has been inserted."))
                .onFailure(throwable -> LOGGER.error("Item insertion failed."));
    }

    public Future<List<JsonObject>> findAllByUser(String userId) {
        return dbClient.find(ITEMS_COLLECTION_NAME, new JsonObject().put("owner", userId));
    }

}
