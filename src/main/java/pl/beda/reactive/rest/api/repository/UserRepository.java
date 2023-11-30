package pl.beda.reactive.rest.api.repository;

import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import pl.beda.reactive.rest.api.model.User;

public class UserRepository {

    private final MongoClient dbClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
    private static final String USERS_COLLECTION_NAME = "users";

    public UserRepository(MongoClient dbClient) {
        this.dbClient = dbClient;
    }

    public Future<String> insert(User user) {
        return dbClient.save(USERS_COLLECTION_NAME, new JsonObject(Json.encode(user)))
                .onSuccess(success -> LOGGER.info("User has been inserted."))
                .onFailure(throwable -> LOGGER.error("User insertion failed."));
    }

    public Future<JsonObject> findByLogin(String login) {
        return dbClient.findOne(USERS_COLLECTION_NAME, new JsonObject().put("login", login), null);
    }

}
