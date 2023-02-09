package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.ItemDto itemDto = new BookingDto.ItemDto(
        );
        BookingDto.UserDto bookerDto = new BookingDto.UserDto();

        if (booking.getItem() != null) {
            itemDto.setId(booking.getItem().getId());
            itemDto.setName(booking.getItem().getName());
        }

        if (booking.getBooker() != null) {
            bookerDto.setId(booking.getBooker().getId());

        }
        log.info("Собираем бронирование");
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemDto(itemDto)
                .bookerDto(bookerDto)
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> booking) {

        return booking
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static Booking toBooking(User booker, Item item, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}
