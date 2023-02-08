package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(long userId,
                                                                     LocalDateTime dateTime1, LocalDateTime dateTime2);
    List<Booking> findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId,
                                                                        LocalDateTime dateTime1, LocalDateTime dateTime2);
    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findTopByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdStatePast(@Param("id") long id, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> findFuture(@Param("userId") long useId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findOwnerAll(long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerFuture(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :currentTime")
    List<Booking> findOwnerPast(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);
}
