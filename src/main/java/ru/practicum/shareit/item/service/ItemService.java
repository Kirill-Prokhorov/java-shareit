package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long user);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDtoWithBooking getItemById(Long itemId, Long userId);

    List<ItemDtoWithBooking> retrieveAllItemByUserId(Long ownerId);

    List<ItemDto> searchItemByKeyword(String keyword);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
