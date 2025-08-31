package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestCreateDto description, Long userId);

    List<ItemRequestWithAnswersDto> getItemRequestsByUserId(Long userId);

    List<ItemRequestDto> getAllItemRequests();

    ItemRequestWithAnswersDto getItemRequestById(Long requestId);
}
