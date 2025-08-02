package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;

@ResponseBody
@ControllerAdvice
@Slf4j
public class HandlerError {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseError notFound(Exception e) {
        log.debug("Not found exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {ConflictException.class})
    public ResponseError conflict(Exception e) {
        log.debug("Conflict exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseError forbidden(Exception e) {
        log.debug("Forbidden exception {}", e.getMessage());
        return new ResponseError(e.getMessage());
    }
}
