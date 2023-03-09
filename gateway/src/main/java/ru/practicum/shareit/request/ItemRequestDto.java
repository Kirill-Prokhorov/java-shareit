package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

}
