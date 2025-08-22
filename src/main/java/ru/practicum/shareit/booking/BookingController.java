package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@ResponseBody
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody @Validated BookingCreateRequestDto booking,
                                    @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.debug("Создание бронирования, id бронирующего {}, данные {}", bookerId, booking);
        return bookingService.createBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@PathVariable("bookingId") @NotNull @Positive Long bookingId,
                                   @RequestParam("approved") @NotNull Boolean isApproved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Подтверждение бронирования с id ={}, isApproved = {}, пользователь {}", bookingId, isApproved, userId);
        return bookingService.approvBooking(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") @Positive @NotNull Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение данных о бронировании c id={}, пользователь с id={}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllUserBooking(
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение всех бронирований пользователя с id={}, статус - {}", userId, state);
        return bookingService.getAllUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllOwnerBooking(
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение всех бронирований владельца с id={}, статус - {}", userId, state);
        return bookingService.getAllOwnerBooking(userId, state);
    }
}
