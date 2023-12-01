package pl.beda.reactive.rest.api.repository;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.beda.reactive.rest.api.model.User;

import java.util.UUID;

public class UserRepositoryTest {

    private static final String USERS_COLLECTION_NAME = "users";

    @Mock
    private MongoClient mongoClient;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Find user by login.")
    void givenLoginWhenGetUserByLoginThenReturnUserSuccessfully() {
        UserRepository userRepository = new UserRepository(mongoClient);

        User user = new User(UUID.randomUUID(), "test", "hash");

        Mockito.when(mongoClient.findOne(USERS_COLLECTION_NAME, new JsonObject().put("login", user.getLogin()), null))
                .thenReturn(Future.future(
                        jsonObjectPromise ->
                                jsonObjectPromise
                                        .complete(JsonObject.mapFrom(user))));

        userRepository
                .findByLogin(user.getLogin())
                .onComplete(ar -> {
                    Assertions.assertNotNull(ar.result());

                    User userResult = ar.result().mapTo(User.class);

                    Assertions.assertEquals(user.getId(), userResult.getId());
                    Assertions.assertEquals(user.getPassword(), userResult.getPassword());
                    Assertions.assertEquals(user.getLogin(), userResult.getLogin());
                });
    }

    @Test
    @DisplayName("Save user.")
    void givenUserWhenSaveUserThenSaveUserSuccessfully() {
        UserRepository userRepository = new UserRepository(mongoClient);

        User user = new User(UUID.randomUUID(), "test", "hash");

        Mockito.when(mongoClient.save(USERS_COLLECTION_NAME, new JsonObject(Json.encode(user))))
                .thenReturn(Future.future(
                        jsonObjectPromise ->
                                jsonObjectPromise
                                        .complete("6569be83f00edd19430eb77c")));

        userRepository
                .insert(user)
                .onComplete(ar -> {
                    Assertions.assertNotNull(ar.result());
                    Assertions.assertEquals("6569be83f00edd19430eb77c", ar.result());
                });
    }

}
