package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        validate(itemRequest);
        itemRequest.setCreated(LocalDateTime.now());

        log.info("Create ItemRequest with ID {}", itemRequestDto.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestDto> getAllMyItemRequest(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));

        Map<Long, List<Item>> requestItemMap = extractItemsToRequests();
        return itemRequestRepository.findByRequester_IdOrderByCreatedAsc(userId)
                .stream()
                .map(itemRequest
                        -> ItemRequestMapper.toItemRequestDto(itemRequest, requestItemMap.get(itemRequest.getId())))
                .collect(Collectors.toList());

    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with ID %s not found", itemRequestId)));
        List<Item> items = itemRepository.findByRequest_Id(itemRequest.getId(), Sort.by("id").descending());

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, int from, int size) {
        if (from >= 0 && size > 0) {

            Map<Long, List<Item>> requestItemMap = extractItemsToRequests();
            return itemRequestRepository.findByRequester_IdNot(userId,
                            PageRequest.of(from / size, size,
                                    Sort.by("created").descending()))
                    .stream()
                    .map(itemRequest
                            -> ItemRequestMapper.toItemRequestDto(itemRequest, requestItemMap.get(itemRequest.getId())))
                    .collect(Collectors.toList());
        }
        else {
            throw new ValidationException("size and from have to positive");
        }
    }

    private Map<Long, List<Item>> extractItemsToRequests() {
        Map<Long, List<Item>> requestItemMap = new HashMap<>();
        List<Item> itemList = itemRepository.findAll();
        List<ItemRequest> itemRequestList = itemRequestRepository.findAll();
        for (ItemRequest itemRequest : itemRequestList) {
            List<Item> itemsToAdd = new ArrayList<>();
            for (Item item : itemList) {
                if (item.getRequest() != null &&
                        item.getRequest().getId().equals(itemRequest.getId())) {
                    itemsToAdd.add(item);
                }
            }
            requestItemMap.put(itemRequest.getId(), itemsToAdd);
        }
        return requestItemMap;
    }

    private void validate(ItemRequest itemRequest) {

        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Description has to be not empty");
        }
    }
}
