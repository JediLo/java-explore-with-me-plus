package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.mapper.RequestMapper;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;
import ru.practicum.explorewithme.request.model.UpdateRequestStatus;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не является инициатором события");
        }

        return requestRepository.findByEventId(eventId)
                .stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        // Нельзя участвовать в своём событии
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может отправить запрос на участие");
        }

        // Нельзя отправить повторную заявку
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Заявка уже существует");
        }

        // Событие должно быть опубликовано
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }

        // Проверка лимита участников
        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository
                    .countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников");
            }
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь не является владельцем запроса");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не является инициатором события");
        }

        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Заявка не относится к данному событию");
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Изменять можно только заявки со статусом PENDING");
            }
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request request : requests) {
            if (dto.getStatus() == UpdateRequestStatus.CONFIRMED) {
                if (limit > 0 && confirmedCount >= limit) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(request);
                    continue;
                }

                request.setStatus(RequestStatus.CONFIRMED);
                confirmed.add(request);
                confirmedCount++;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(request);
            }
        }

        requestRepository.saveAll(requests);

        return RequestMapper.toEventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        // 1. Проверка дублирования заявки
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Нельзя добавить повторный запрос на участие");
        }

        // 2. Инициатор не может подавать заявку на свое событие
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        // 3. Заявки можно подавать только на опубликованные события
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        // 4. Проверка лимита участников
        int limit = event.getParticipantLimit();
        if (limit > 0) {
            long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedRequests >= limit) {
                throw new ConflictException("У события достигнут лимит запросов на участие");
            }
        }

        // 5. Формирование заявки и определение её статуса
        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        // Если пре-модерация отключена или лимит равен 0 (безлимит), статус сразу CONFIRMED
        if (!event.getRequestModeration() || limit == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь может отменить только свой запрос на участие");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(requestRepository.save(request));
    }
}
