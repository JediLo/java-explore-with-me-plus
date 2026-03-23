package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto dto);

    List<EventShortDto> getEvents(long userId, int from, int size);
}
