package pl.beda.reactive.rest.api.service;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import pl.beda.reactive.rest.api.dto.input.InputItemDto;
import pl.beda.reactive.rest.api.model.Item;
import pl.beda.reactive.rest.api.repository.ItemRepository;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoutingContext event;

    @Mock
    private HttpServerRequest request;

    @Mock
    private HttpServerResponse response;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        doReturn(request).when(event).request();
        doReturn(response).when(event).response();
    }

    @Test
    @DisplayName("Item add.")
    void givenItemDataWhenTryToAddItemByUserThenAddItemSuccessfully() {
        try (MockedStatic<ResponseUtils> responseUtilsMockedStatic = Mockito.mockStatic(ResponseUtils.class)) {
            responseUtilsMockedStatic.when(() -> ResponseUtils.buildNoContentResponse(event)).thenAnswer((Answer<Void>) invocation -> null);

            Mockito.when(tokenService.getUserFromToken(event))
                    .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete(User.create(new JsonObject().put("UUID", UUID.randomUUID())))));

            Mockito.when(itemRepository.insert(Mockito.any(Item.class)))
                    .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete("6569be83f00edd19430eb77c")));

            ItemService itemService = new ItemService(tokenService, itemRepository);

            itemService.add(event, new InputItemDto("test"));

            responseUtilsMockedStatic.verify(() -> ResponseUtils.buildNoContentResponse(event));
        }
    }

    @Test
    @DisplayName("Items read by user.")
    void givenUserCredentialsWhenLoginThenAuthSuccessfully() {
        try (MockedStatic<ResponseUtils> responseUtilsMockedStatic = Mockito.mockStatic(ResponseUtils.class)) {
            responseUtilsMockedStatic.when(() -> ResponseUtils.buildNoContentResponse(event)).thenAnswer((Answer<Void>) invocation -> null);
            UUID userId = UUID.randomUUID();

            Item item1 = new Item(UUID.randomUUID(), userId, "test");
            Item item2 = new Item(UUID.randomUUID(), userId, "test");

            Mockito.when(tokenService.getUserFromToken(event))
                    .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete(User.create(new JsonObject().put("UUID", userId)))));

            Mockito.when(itemRepository.findAllByUser(Mockito.anyString()))
                    .thenReturn(Future.future(
                            jsonObjectPromise ->
                                    jsonObjectPromise
                                            .complete(List.of(JsonObject.mapFrom(item1), JsonObject.mapFrom(item2)))));

            ItemService itemService = new ItemService(tokenService, itemRepository);

            itemService.getAll(event);

            responseUtilsMockedStatic.verify(() -> ResponseUtils.buildOkResponse(any(RoutingContext.class), any(List.class)));
        }
    }

}
