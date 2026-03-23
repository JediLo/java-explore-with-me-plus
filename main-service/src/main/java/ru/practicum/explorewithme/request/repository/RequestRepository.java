package ru.practicum.explorewithme.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    interface EventConfirmedCount {
        Long getEventId();
        Long getCnt();
    }

    @Query("select r.event.id as eventId, count(r.id) as cnt " +
            "from Request r " +
            "where r.event.id in :eventIds and r.status = :status " +
            "group by r.event.id")
    List<EventConfirmedCount> countByEventIdsAndStatus(@Param("eventIds") List<Long> eventIds,
                                                      @Param("status") RequestStatus status);
}
