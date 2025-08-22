package ru.practicum.shareit.booking.dal;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerId(Long bookerId, Sort sort);

    Collection<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime now, LocalDateTime nowPlus, Sort sort);

    Collection<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    Collection<Booking> findByItem_OwnerId(Long ownerId, Sort sort);

    Collection<Booking> findByItem_OwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItem_OwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findByItem_OwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);


    List<Booking> findByBookerIdAndItemIdAndEndBeforeAndStatus(
            long bookerId, long itemId, LocalDateTime now, BookingStatus status);
}
