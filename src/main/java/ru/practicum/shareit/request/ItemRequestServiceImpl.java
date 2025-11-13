package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestCreateDto description, Long userId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(getUserOrThrow(userId));
        itemRequest.setDescription(description.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithAnswersDto> getItemRequestsByUserId(Long userId) {
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toDtoWithAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        return requestRepository.findAll().stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestById(Long requestId) {
        return ItemRequestMapper.toDtoWithAnswers(
                requestRepository.findById(requestId).orElseThrow(
                        () -> new NotFoundException("Запрос не найден с id = " + requestId)));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id = " + userId));
    }
}
