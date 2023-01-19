package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDbStorage itemDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public ItemServiceImpl(ItemDbStorage itemDbStorage, UserDbStorage userDbStorage) {
        this.itemDbStorage = itemDbStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<ItemDto> getListItemByUserId(Long userId) {
        return itemDbStorage.getListItemByUserId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemDbStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userDbStorage.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new BadRequestException("Название вещи не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new BadRequestException("Описание вещи не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new BadRequestException("Доступность вещи не может быть пустой");
        }
        return ItemMapper.toItemDto(itemDbStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userDbStorage.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemDbStorage.updateItem(itemId, item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemDbStorage.searchItem(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

}