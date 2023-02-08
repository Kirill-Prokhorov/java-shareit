package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentDbStorage;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDbStorage itemDbStorage;
    private final UserDbStorage userDbStorage;
    private final BookingDbStorage bookingDbStorage;
    private final CommentDbStorage commentDbStorage;

    @Transactional
    public List<ItemDtoWithBooking> getListItemByUserId(Long userId) {
        return itemDbStorage.findByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> {
                            List<Comment> comments = getCommentsByItemId(item);
                            Booking lastBooking = bookingDbStorage
                                    .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                            Booking nextBooking = bookingDbStorage
                                    .findTopByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
                            return ItemMapper.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {
        Item item = itemDbStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
        List<Comment> commentList = getCommentsByItemId(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Booking lastBooking = bookingDbStorage.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, localDateTime);
            Booking nextBooking = bookingDbStorage.findTopByItem_IdAndStartAfterOrderByStartAsc(itemId, localDateTime);
            return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
        } else {
            return ItemMapper.toItemDtoWithBooking(commentList, null, null, item);
        }
    }

    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность вещи не может быть пустой");
        }
        item.setOwner(user);
        log.info("Создали вещь с ID {}", item.getId());
        return ItemMapper.toItemDto(itemDbStorage.save(item));
    }

    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = itemDbStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с ID %s не найден", itemId)));
        User user = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = ItemMapper.toItem(itemDto, user);
        if (user != null && !updatedItem.getOwner().getId().equals(userId))
            throw new NotFoundException("Предмет не доступен для брони");

        String updatedDescription = item.getDescription();
        if (updatedDescription != null && !updatedDescription.isBlank()) {
            updatedItem.setDescription(updatedDescription);
        }
        String updatedName = item.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedItem.setName(updatedName);
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemDbStorage.save(updatedItem));
    }

    @Transactional
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDbStorage.searchItem(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {

        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("Пустые комментарии недопустимы");
        }
        User user = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = itemDbStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с ID %s не найден", itemId)));
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        List<Booking> booking = bookingDbStorage.findByBookerIdStatePast(comment.getUser().getId(),
                LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new ValidationException("Не найдено брони у этого пользователя");
        }

        comment.setCreated(LocalDateTime.now());
        commentDbStorage.save(comment);
        log.info("Создан комментарий с ID {}", comment.getId());

        return CommentMapper.toCommentDto(comment);
    }

    public List<Comment> getCommentsByItemId(Item item) {
        return commentDbStorage.findByItem_IdOrderByCreatedDesc(item.getId());
    }
}

