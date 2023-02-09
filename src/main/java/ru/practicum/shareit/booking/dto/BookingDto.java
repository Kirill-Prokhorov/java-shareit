package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto itemDto;
    private Long itemId;
    private UserDto bookerDto;
    private Status status;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDto {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private Long id;
    }
}

