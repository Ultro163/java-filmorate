package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

@Data
@Builder
public class Event {
    @NotNull
    private long eventId;
    @NotNull
    private long userId;
    @NotNull
    private long timestamp;
    @NotNull
    private EventTypes eventType;
    @NotNull
    private OperationTypes operation;
    @NotNull
    private long entityId;
}