package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        log.debug("Метод сервиса. Добавление вещи {} пользователем с id {}", itemDto, userId);
        User user = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
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
            List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
            List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);
            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(lastBookings.get(0)));
            }
            if (!nextBookings.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(nextBookings.get(0)));
            }
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        log.debug("Метод сервиса. Получение всех вещей пользователя с id {}", userId);
        getUserOrThrow(userId);
        return itemRepository.findAllItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
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

        List<Booking> bookings = bookingRepository.findBookingsForComment(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ForbiddenException(
                    "Вы не можете комментировать эту вещь, так как у вас нет завершенных бронирований.");
        }

        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .author(user)
                .item(item)
                .text(commentCreateDto.getText()).build();
        return CommentMapper.mapToDto(commentRepository.save(comment));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден!"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!"));
    }

    private boolean checkStringNotNullAndNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}
