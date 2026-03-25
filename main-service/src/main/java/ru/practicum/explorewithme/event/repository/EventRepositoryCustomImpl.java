package ru.practicum.explorewithme.event.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.category.model.QCategory;
import ru.practicum.explorewithme.event.dto.EventAdminSettingSearchDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.QEvent;
import ru.practicum.explorewithme.user.model.QUser;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Event> findEventsToAdmin(EventAdminSettingSearchDto settingSearch) {

        QEvent event = QEvent.event;
        QUser user = QUser.user;
        QCategory category = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();

        if (settingSearch.getUserIds() != null && !settingSearch.getUserIds().isEmpty()) {
            builder.and(event.initiator.id.in(settingSearch.getUserIds()));
        }

        if (settingSearch.getStates() != null && !settingSearch.getStates().isEmpty()) {
            builder.and(event.state.in(settingSearch.getStates()));
        }

        if (settingSearch.getCategoriesIds() != null && !settingSearch.getCategoriesIds().isEmpty()) {
            builder.and(event.category.id.in(settingSearch.getCategoriesIds()));
        }

        if (settingSearch.getRangeStart() != null) {
            builder.and(event.eventDate.goe(settingSearch.getRangeStart()));
        }

        if (settingSearch.getRangeEnd() != null) {
            builder.and(event.eventDate.loe(settingSearch.getRangeEnd()));
        }

        return queryFactory
                .selectFrom(event)
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.category, category).fetchJoin()
                .where(builder)
                .orderBy(event.eventDate.desc())
                .offset(settingSearch.getFrom())
                .limit(settingSearch.getSize())
                .fetch();
    }
}
