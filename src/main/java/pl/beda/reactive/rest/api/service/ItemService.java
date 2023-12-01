package pl.beda.reactive.rest.api.service;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import pl.beda.reactive.rest.api.dto.input.InputItemDto;
import pl.beda.reactive.rest.api.dto.output.OutputItemDto;
import pl.beda.reactive.rest.api.model.Item;
import pl.beda.reactive.rest.api.repository.ItemRepository;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

import java.util.UUID;
import java.util.stream.Collectors;

public class ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final TokenService tokenService;

    public ItemService(TokenService tokenService, ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.tokenService = tokenService;
    }

    public void add(RoutingContext rc, InputItemDto inputItemDto) {
        tokenService.getUserFromToken(rc)
                .onSuccess(user -> {
                    Item item = new Item();
                    item.setId(UUID.randomUUID());
                    item.setOwner(UUID.fromString(user.principal().getString("UUID")));
                    item.setName(inputItemDto.getTitle());

                    itemRepository.insert(item)
                            .onSuccess(success -> {
                                LOGGER.info("Item has been created.");
                                ResponseUtils.buildNoContentResponse(rc);
                            })
                            .onFailure(throwable -> {
                                LOGGER.error("Item creation failed.");
                                ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Cannot create item.");
                            });
                })
                .onFailure(throwable -> {
                    LOGGER.error("Item creation failed.");
                    ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Cannot create item.");
                });
    }

    public void getAll(RoutingContext rc) {
        tokenService.getUserFromToken(rc)
                .onSuccess(user -> itemRepository.findAllByUser(user.principal().getString("UUID"))
                        .onSuccess(items -> {
                            LOGGER.info("Items has been read.");
                            ResponseUtils.buildOkResponse(rc, items.stream()
                                    .map(item -> new OutputItemDto(UUID.fromString(item.getString("id")), item.getString("name")))
                                    .collect(Collectors.toList()));
                        })
                        .onFailure(throwable -> {
                            LOGGER.error("Cannot read items.");
                            ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Cannot read items.");
                        }))
                .onFailure(throwable -> {
                    LOGGER.error("Cannot read items.");
                    ResponseUtils.buildErrorResponse(rc, HttpResponseStatus.BAD_REQUEST.code(), "Cannot read items.");
                });
    }

}
