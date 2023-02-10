package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDbStorage bookingDbStorage;
    private final UserDbStorage userDbStorage;
    private final ItemDbStorage itemDbStorage;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {

        User booker = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = itemDbStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID %s не найдена", bookingDto.getItemId())));
        Booking booking = BookingMapper.toBooking(booker, item, bookingDto);

        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь не доступна");
        }
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Дата начала бронирования позже окончания");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования уже прошла");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования уже прошла");
        }
        booking.setStatus(Status.WAITING);
        log.info("Вещь с  ID {} забронирована", item.getId());

        return BookingMapper.toBookingDto(bookingDbStorage.save(booking));
    }

    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем");
        }
        if (booking.getStatus().equals(Status.APPROVED) && isApproved) {
            throw new ValidationException("Уже одобрено");
        }
        if (booking.getStatus().equals(Status.REJECTED) && !isApproved) {
            throw new ValidationException("Уже отклонено");
        }
        setApprovedStatus(booking, isApproved);
        log.info("Бронирование с ID {} обновлено", booking.getId());

        return BookingMapper.toBookingDto(bookingDbStorage.save(booking));
    }

    @Transactional
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Бронирование с ID %s не найдено", bookingId));
        }
    }

    @Transactional
    public Booking getBookingById(Long bookingId) {
        return bookingDbStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID %s не найдено",
                        bookingId)));
    }

    public List<Booking> getAllUser(long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        return bookingDbStorage.findAllByBooker_IdOrderByStartDesc(userId);
    }

    public State getStateByStr(String stateStr) {

        State state;
        if (stateStr == null) {
            state = State.ALL;
        } else {
            try {
                state = State.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Unknown state: %s", stateStr));
            }
        }

        return state;
    }

    public List<BookingDto> getAllBookingByUser(Long userId, String stateStr) {

        State state = getStateByStr(stateStr);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = getAllUser(userId);
                break;
            case PAST:
                bookings = bookingDbStorage.findByBookerIdStatePast(userId, now);
                break;
            case WAITING:
                bookings = bookingDbStorage.findByBooker_IdAndStatus(userId, Status.WAITING);
                break;
            case CURRENT:
                bookings = bookingDbStorage.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now,now);
                break;
            case REJECTED:
                bookings = bookingDbStorage.findByBooker_IdAndStatus(userId, Status.REJECTED);
                break;
            case FUTURE:
                bookings = bookingDbStorage.findFuture(userId, now);
                break;
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    public List<BookingDto> getAllBookingByOwner(Long userId, String stateStr) {

        State state = getStateByStr(stateStr);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingDbStorage.findOwnerAll(userId);
                break;
            case FUTURE:
                bookings = bookingDbStorage.findOwnerFuture(userId, now);
                break;
            case CURRENT:
                bookings = bookingDbStorage.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, now, now);
                break;
            case WAITING:
                bookings = bookingDbStorage.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING);
                break;
            case PAST:
                bookings = bookingDbStorage.findOwnerPast(userId, now);
                break;
            case REJECTED:
                bookings = bookingDbStorage.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED);
                break;
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    @Transactional
    public List<BookingDto> getAllBookingByOwnerId(Long ownerId, String stateStr) {

        userDbStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", ownerId)));
        List<Booking> bookings = bookingDbStorage.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено");
        }
        return getAllBookingByOwner(ownerId, stateStr);
    }

    @Transactional
    public List<BookingDto> getAllBookingByUserId(Long userId, String stateStr) {

        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        List<Booking> bookings = bookingDbStorage.findAllByBooker_IdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено");
        }
        return getAllBookingByUser(userId, stateStr);
    }


    private void setApprovedStatus(Booking booking, Boolean isApproved) {
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
    }
}