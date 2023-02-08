package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemDbStorage extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("SELECT i FROM Item i WHERE i.available = true " +
            " AND (LOWER(i.name) LIKE LOWER(CONCAT('%',:text,'%') ) " +
            " OR LOWER(i.description) LIKE LOWER(CONCAT('%',:text,'%') ) )")
    Collection<Item> searchItem(@Param("text") String text);
}
