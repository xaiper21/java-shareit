package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exception.*;

@ResponseBody
@ControllerAdvice
@Slf4j
public class HandlerError {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseError notFound(NotFoundException e) {
        log.debug("Not found exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {UnsupportedStatusException.class, BadRequestException.class})
    public ResponseError badRequest(Exception e) {
        log.debug("Bad Request exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {ConflictException.class})
    public ResponseError conflict(ConflictException e) {
        log.debug("Conflict exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseError forbidden(ForbiddenException e) {
        log.debug("Forbidden exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseError allOtherExceptions(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return new ResponseError("Произошла внутренняя ошибка сервера.");
    }
}
