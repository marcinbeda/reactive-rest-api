package pl.beda.reactive.rest.api.handler;

import io.vertx.ext.web.RoutingContext;
import pl.beda.reactive.rest.api.dto.input.InputItemDto;
import pl.beda.reactive.rest.api.service.ItemService;

public class ItemHandler {

    private final ItemService itemService;

    public ItemHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    public void addItem(RoutingContext rc) {
        final InputItemDto inputItemDto = rc.getBodyAsJson().mapTo(InputItemDto.class);
        itemService.add(rc, inputItemDto);
    }

    public void readAllItems(RoutingContext rc) {
        itemService.getAll(rc);
    }

}
