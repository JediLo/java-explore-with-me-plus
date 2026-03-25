package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.StatsClient;
import ru.practicum.dto.StatsParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.category.repository.CategoryRepository;
import ru.practicum.explorewithme.common.paging.OffsetBasedPageRequest;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.mapper.EventMapper;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exceptions.ConditionsNotMetException;
import ru.practicum.explorewithme.exceptions.ForbiddenException;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.request.model.RequestStatus;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private static final int MIN_HOURS_BEFORE_EVENT = 2; // openApi 1353
    private static final LocalDateTime STATS_START = LocalDateTime.of(1999, 1, 1, 0, 0);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto addEvent(long userId, NewEventDto dto) {
        if (dto.getEventDate() == null) {
            throw new IllegalArgumentException("eventDate must not be null");
        }
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(MIN_HOURS_BEFORE_EVENT))) {
            throw new ConditionsNotMetException("Event date must be at least 2 hours in the future");
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> userNotFound(userId));

        long categoryId = dto.getCategory();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> categoryNotFound(categoryId));

        Event event = EventMapper.toEvent(dto, initiator, category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event saved = eventRepository.save(event);

        return EventMapper.toEventFullDto(saved, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> userNotFound(userId));

        Pageable pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, EventStatsDto> statsByEventId = loadEventStatsByEventIds(eventIds);

        return events.stream()
                .map(event -> {
                    EventStatsDto stats = statsByEventId.get(event.getId());
                    long confirmed = stats == null ? 0L : stats.getConfirmedRequests();
                    long views = stats == null ? 0L : stats.getViews();
                    return EventMapper.toEventShortDto(event, confirmed, views);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> findAllEventsToAdmin(EventAdminSettingSearchDto settingSearchDto) {
        EventAdminSettingSearchDto dtoWithDefaultValue = addDefaultValueToSettingDto(settingSearchDto);
        List<Event> events = eventRepository.findEventsToAdmin(dtoWithDefaultValue);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, EventStatsDto> statsByEventId = loadEventStatsByEventIds(eventIds);

        return events.stream()
                .map(event -> {
                    EventStatsDto stats = statsByEventId.get(event.getId());
                    long confirmed = stats == null ? 0L : stats.getConfirmedRequests();
                    long views = stats == null ? 0L : stats.getViews();
                    return EventMapper.toEventFullDto(event, confirmed, views);
                })
                .toList();
    }

    @Override
    public EventFullDto updateEventByIdToAdmin(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> eventNotFound(eventId));
        if (event.getState() == EventState.PUBLISHED) {
            throw eventForbiddenPublished(event.getState());
        }

        if (adminRequest.getAnnotation() != null) {
            event.setAnnotation(adminRequest.getAnnotation());
        }
        Long categoryId = adminRequest.getCategory();
        if (categoryId != null) {
            event.setCategory(categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() -> categoryNotFound(categoryId)));
        }
        if (adminRequest.getDescription() != null) {
            event.setDescription(adminRequest.getDescription());
        }
        if (adminRequest.getEventDate() != null) {
            event.setEventDate(adminRequest.getEventDate());
        }
        if (adminRequest.getLocation() != null) {
            event.setLat(adminRequest.getLocation().getLat());
            event.setLon(adminRequest.getLocation().getLon());
        }
        if (adminRequest.getPaid() != null) {
            event.setPaid(adminRequest.getPaid());
        }
        if (adminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminRequest.getParticipantLimit());
        }
        if (adminRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminRequest.getRequestModeration());
        }
        if (adminRequest.getStateAction() != null) {
            switch (adminRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConditionsNotMetException("Cannot publish the event because it's not in the right state: " + event.getState());
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConditionsNotMetException("Cannot reject a published event");
                    }
                    event.setState(EventState.CANCELED);
                }
                case SEND_TO_REVIEW -> {
                    event.setState(EventState.PENDING);
                }
                case CANCEL_REVIEW -> {
                    event.setState(EventState.CANCELED);
                }
            }
        }
        if (adminRequest.getTitle() != null) {
            event.setTitle(adminRequest.getTitle());
        }
        Event updatedEvent = eventRepository.save(event);
        List<Long> eventIds = new ArrayList<>();
        eventIds.add(updatedEvent.getId());
        Map<Long, EventStatsDto> statsByEventId = loadEventStatsByEventIds(eventIds);
        EventStatsDto stats = statsByEventId.get(updatedEvent.getId());
        return EventMapper.toEventFullDto(updatedEvent, stats.getConfirmedRequests(), stats.getViews());
    }


    private EventAdminSettingSearchDto addDefaultValueToSettingDto(EventAdminSettingSearchDto settingSearchDto) {
        return new EventAdminSettingSearchDto(
                settingSearchDto.getUserIds(),
                settingSearchDto.getStates(),
                settingSearchDto.getCategoriesIds(),
                settingSearchDto.getRangeStart(),
                settingSearchDto.getRangeEnd(),
                settingSearchDto.getFrom() != null ? settingSearchDto.getFrom() : 0,
                settingSearchDto.getSize() != null ? settingSearchDto.getSize() : 10
        );
    }

    private Map<Long, EventStatsDto> loadEventStatsByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> confirmedByEventId = getConfirmedRequestsByEventIds(eventIds);  //todo протестить по факту
        Map<Long, Long> viewsByEventId = getViewsByEventIds(eventIds);

        Map<Long, EventStatsDto> result = new HashMap<>();
        for (Long eventId : eventIds) {
            result.put(eventId, new EventStatsDto(
                    confirmedByEventId.getOrDefault(eventId, 0L),
                    viewsByEventId.getOrDefault(eventId, 0L)
            ));
        }
        return result;
    }

    private Map<Long, Long> getConfirmedRequestsByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<RequestRepository.EventConfirmedCount> rows =
                requestRepository.countByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED);

        Map<Long, Long> result = new HashMap<>();
        for (RequestRepository.EventConfirmedCount row : rows) {
            result.put(row.getEventId(), row.getCnt());
        }
        return result;
    }

    private Map<Long, Long> getViewsByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        StatsParamDto params = new StatsParamDto(STATS_START, LocalDateTime.now(), uris, false);

        List<ViewStatsDto> stats;
        try {
            stats = statsClient.getStats(params);
            log.debug("Stats response: size={}", stats == null ? 0 : stats.size());
        } catch (RestClientException ex) {
            log.warn("Stats request failed; returning views=0. Message: {}", ex.getMessage());
            return Collections.emptyMap();
        }
        if (stats == null || stats.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> result = new HashMap<>();
        for (ViewStatsDto row : stats) {
            if (row == null || row.getUri() == null || row.getHits() == null) {
                continue;
            }
            Long eventId = extractEventId(row.getUri());
            if (eventId == null) {
                continue;
            }
            result.merge(eventId, row.getHits(), Long::sum);
        }
        return result;
    }

    private Long extractEventId(String uri) {
        int lastSlash = uri.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == uri.length() - 1) {
            return null;
        }
        String idPart = uri.substring(lastSlash + 1);
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private NotFoundException eventNotFound(Long eventId) {
        return new NotFoundException("Event with id=" + eventId + " was not found");
    }

    private ForbiddenException eventForbiddenPublished(EventState state) {
        return new ForbiddenException("Cannot publish the event because it's not in the right state: " + state);
    }

    private NotFoundException categoryNotFound(Long categoryId) {
        return new NotFoundException("Category with id=" + categoryId + " was not found");
    }

    private NotFoundException userNotFound(Long userId) {
        return new NotFoundException("User with id=" + userId + " was not found");
    }
}
