package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDbStorage {

    List<Item> getListItemByUserId(Long userId);

    Item getItemById(Long itemId);

    Item createItem(Item item);

    Item updateItem(Long itemId, Item item);

    List<Item> searchItem(String text);

}
