package ru.practicum.explorewithme.event.repository;

import ru.practicum.explorewithme.event.dto.EventAdminSettingSearchDto;
import ru.practicum.explorewithme.event.model.Event;

import java.util.List;


public interface EventRepositoryCustom {

    List<Event> findEventsToAdmin(EventAdminSettingSearchDto settingSearch);
}
