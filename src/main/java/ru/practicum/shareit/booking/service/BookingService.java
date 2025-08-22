package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long bookerId, BookingCreateRequestDto booking);

    BookingDto approvBooking(Long bookingId, Long userId, Boolean isApproved);

    BookingDto getBooking(Long bookingId, Long userId);

    Collection<BookingDto> getAllUserBooking(Long userId, BookingState state);

    Collection<BookingDto> getAllOwnerBooking(Long userId, BookingState state);
}

