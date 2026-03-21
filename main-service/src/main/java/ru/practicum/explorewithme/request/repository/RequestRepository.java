package ru.practicum.explorewithme.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);
}
