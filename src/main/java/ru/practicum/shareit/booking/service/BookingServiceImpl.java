package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Map<BookingState, Function<Long, Collection<Booking>>> bookerBookingStrategies;
    private final Map<BookingState, Function<Long, Collection<Booking>>> ownerBookingStrategies;

    public BookingServiceImpl(
            BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        Sort sort = Sort.by("start").descending();

        this.bookerBookingStrategies = Map.of(
                BookingState.ALL,
                userId -> bookingRepository.findByBookerId(userId, sort),
                BookingState.PAST,
                userId -> bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort),
                BookingState.FUTURE,
                userId -> bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort),
                BookingState.CURRENT,
                userId -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sort),
                BookingState.WAITING,
                userId -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort),
                BookingState.REJECTED,
                userId -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort)
        );

        this.ownerBookingStrategies = Map.of(
                BookingState.ALL,
                userId -> bookingRepository.findByItem_OwnerId(userId, sort),
                BookingState.PAST,
                userId -> bookingRepository.findByItem_OwnerIdAndEndBefore(userId, LocalDateTime.now(), sort),
                BookingState.FUTURE,
                userId -> bookingRepository.findByItem_OwnerIdAndStartAfter(userId, LocalDateTime.now(), sort),
                BookingState.CURRENT,
                userId -> bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sort),
                BookingState.WAITING,
                userId -> bookingRepository.findByItem_OwnerIdAndStatus(userId, BookingStatus.WAITING, sort),
                BookingState.REJECTED,
                userId -> bookingRepository.findByItem_OwnerIdAndStatus(userId, BookingStatus.REJECTED, sort)
        );
    }

    @Override
    public BookingDto createBooking(Long bookerId, BookingCreateRequestDto booking) {
        Item item = findItemOrThrow(booking.getItemId());
        User booker = findUserOrThrow(bookerId);
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ForbiddenException("Владелец не может бронировать свою собственную вещь");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше или равняться дате начала.");
        }
        Booking booking1 = bookingRepository.save(BookingMapper.toBooking(booking, booker, item));
        return BookingMapper.toBookingDto(booking1);
    }

    @Override
    public BookingDto approvBooking(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new BadRequestException("Статус вещи должен быть  WAITING");
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("У вас нет прав к ресурсу");
        } else {
            if (isApproved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = findBookingOrThrow(bookingId);
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (!(userId == ownerId || userId == bookerId)) throw new ForbiddenException("У вас нет доступа к ресурсу");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllUserBooking(Long userId, BookingState state) {
        validateUserExists(userId);
        return getBookingsByState(userId, state, bookerBookingStrategies);
    }

    @Override
    public Collection<BookingDto> getAllOwnerBooking(Long userId, BookingState state) {
        validateUserExists(userId);
        return getBookingsByState(userId, state, ownerBookingStrategies);
    }

    private Collection<BookingDto> getBookingsByState(
            Long userId, BookingState state, Map<BookingState, Function<Long, Collection<Booking>>> strategies) {
        Function<Long, Collection<Booking>> getBookingsFunction = strategies.get(state);
        if (getBookingsFunction == null) {
            throw new UnsupportedStatusException("Статус не поддерживается status = " + state);
        }

        Collection<Booking> result = getBookingsFunction.apply(userId);

        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден с id = " + userId);
        }
    }

    private Item findItemOrThrow(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Бронируемая вещь не найдена с id = " + itemId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден с id = " + userId));
    }

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено с id = " + bookingId));
    }
}
