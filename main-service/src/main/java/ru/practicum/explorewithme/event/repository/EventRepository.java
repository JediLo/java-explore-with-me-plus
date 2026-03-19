package ru.practicum.explorewithme.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
