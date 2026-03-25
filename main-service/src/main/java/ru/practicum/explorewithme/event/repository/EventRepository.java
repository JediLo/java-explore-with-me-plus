package ru.practicum.explorewithme.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import ru.practicum.explorewithme.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    @EntityGraph(attributePaths = {"initiator", "category"})
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
