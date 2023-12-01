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
import pl.beda.reactive.rest.api.model.Item;

import java.util.List;
import java.util.UUID;

public class ItemRepositoryTest {

    private static final String ITEMS_COLLECTION_NAME = "items";

    @Mock
    private MongoClient mongoClient;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Read user items.")
    void givenUserItemsWhenGetUserItemsByUserIdThenReturnUserItemsSuccessfully() {
        ItemRepository itemRepository = new ItemRepository(mongoClient);

        UUID userId = UUID.randomUUID();

        Item item1 = new Item(UUID.randomUUID(), userId, "test");
        Item item2 = new Item(UUID.randomUUID(), userId, "test");

        Mockito.when(mongoClient.find(ITEMS_COLLECTION_NAME, new JsonObject().put("owner", userId.toString())))
                .thenReturn(Future.future(
                        jsonObjectPromise ->
                                jsonObjectPromise
                                        .complete(List.of(JsonObject.mapFrom(item1), JsonObject.mapFrom(item2)))));

        itemRepository
                .findAllByUser(userId.toString())
                .onComplete(ar -> {
                    Assertions.assertNotNull(ar.result());

                    Item itemResult1 = ar.result().get(0).mapTo(Item.class);
                    Item itemResult2 = ar.result().get(1).mapTo(Item.class);

                    Assertions.assertEquals(item1, itemResult1);
                    Assertions.assertEquals(item2, itemResult2);
                });
    }

    @Test
    @DisplayName("Save item.")
    void givenItemWhenSaveItemThenSaveItemSuccessfully() {
        ItemRepository userRepository = new ItemRepository(mongoClient);

        Item item = new Item(UUID.randomUUID(), UUID.randomUUID(), "test");

        Mockito.when(mongoClient.save(ITEMS_COLLECTION_NAME, new JsonObject(Json.encode(item))))
                .thenReturn(Future.future(
                        jsonObjectPromise ->
                                jsonObjectPromise
                                        .complete("6569be83f00edd19430eb77c")));

        userRepository
                .insert(item)
                .onComplete(ar -> {
                    Assertions.assertNotNull(ar.result());
                    Assertions.assertEquals("6569be83f00edd19430eb77c", ar.result());
                });
    }

}
