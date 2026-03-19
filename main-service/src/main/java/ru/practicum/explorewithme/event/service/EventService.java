package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;


public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto dto);
}
