package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.dto.*;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto dto);

    List<EventShortDto> getEvents(long userId, int from, int size);

    List<EventFullDto> findAllEventsToAdmin(EventAdminSettingSearchDto settingSearchDto);

    EventFullDto updateEventByIdToAdmin(Long eventId, UpdateEventAdminRequest adminRequest);
}
