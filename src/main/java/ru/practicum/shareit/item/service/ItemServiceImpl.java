package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemCreateDto itemDto, Long userId) {
        log.debug("Метод сервиса. Добавление вещи {} пользователем с id {}", itemDto, userId);
        User user = getUserOrThrow(userId);
        ItemRequest itemRequest = itemDto.getRequestId() != null ? getItemRequestOrThrow(itemDto.getRequestId()) : null;
        Item item = ItemMapper.toItem(itemDto, itemRequest);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequestDto itemDto) {
        log.debug("Метод сервиса. Обновление вещи с id {} пользователем с id {}, данные обновления: {}",
                itemId, userId, itemDto);
        getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("У вас нет доступа к ресурсу");
        }

        if (checkStringNotNullAndNotEmpty(itemDto.getName())) item.setName(itemDto.getName());
        if (checkStringNotNullAndNotEmpty(itemDto.getDescription())) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        log.info("Получение вещи с id={} для пользователя с id={}", itemId, userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена."));

        Collection<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> itemBookings = bookingRepository.findByItemOwnerIdAndStatusAndItem_Id(
                    userId, BookingStatus.APPROVED, itemId);

            itemBookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .ifPresent(b -> itemDto.setLastBooking(BookingMapper.toBookingDto(b)));

            itemBookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .ifPresent(b -> itemDto.setNextBooking(BookingMapper.toBookingDto(b)));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        log.info("Получение всех вещей для пользователя с id={}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден.");
        }

        Collection<Item> userItems = itemRepository.findByOwnerId(userId);

        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> approvedBookings = bookingRepository.findAllByItemInAndStatus(userItems, BookingStatus.APPROVED);

        Collection<Comment> itemComments = commentRepository.findAllByItemIn(userItems);

        Map<Long, List<Booking>> bookingsByItemId = approvedBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsByItemId = itemComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return userItems.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList()).stream()
                            .map(CommentMapper::mapToDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(comments);

                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());

                    itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .ifPresent(b -> itemDto.setLastBooking(BookingMapper.toBookingDto(b)));

                    itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .ifPresent(b -> itemDto.setNextBooking(BookingMapper.toBookingDto(b)));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.debug("Метод сервиса. Поиск вещей по входящей строке - {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, long userId, CommentCreateRequestDto commentCreateDto) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (bookings.isEmpty()) {
            throw new BadRequestException(
                    "Вы не можете комментировать эту вещь, так как у вас нет завершенных бронирований.");
        }

        return CommentMapper.mapToDto(commentRepository.save(CommentMapper.buildComment(user, item, commentCreateDto)));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!"));
    }

    private boolean checkStringNotNullAndNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private ItemRequest getItemRequestOrThrow(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не найден с id = " + requestId));
    }
}
