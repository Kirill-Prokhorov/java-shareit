package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllMyItemRequest(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);

    List<ItemRequestDto> findAll(Long userId, int from, int size);
}
