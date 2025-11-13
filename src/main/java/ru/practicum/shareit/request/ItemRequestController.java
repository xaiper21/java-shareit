package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@ResponseBody
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestCreateDto description,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.addItemRequest(description, userId);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        return requestService.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getRequestById(@PathVariable("requestId") Long requestId) {
        return requestService.getItemRequestById(requestId);
    }

}
