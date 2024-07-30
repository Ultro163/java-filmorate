package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final Exception e) {
        log.warn(e.getMessage());
        String errorMessage;

        if (e instanceof MethodArgumentNotValidException ex) {
            errorMessage = ex.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> String.format("Error in the field '%s': %s", fieldError.getField(),
                            fieldError.getDefaultMessage()))
                    .findFirst()
                    .orElse("Incorrect data");
        } else if (e instanceof MissingServletRequestParameterException ex) {
            errorMessage = String.format("Required parameter missing: %s", ex.getParameterName());
        } else {
            errorMessage = e.getMessage();
        }
        return new ErrorResponse(errorMessage);
    }
}