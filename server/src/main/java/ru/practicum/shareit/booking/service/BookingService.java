package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto updateBooking(Long bookingDto, Long userId, Boolean isApproved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingByOwnerId(Long userId, BookingState state, int from, int size);

    List<BookingDto> getAllBookingByUserId(Long userId, BookingState state, int from, int size);
}
