package ru.practicum.explorewithme.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.model.Request;

import java.util.List;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> confirmed,
                                                                                  List<Request> rejected) {
        return new EventRequestStatusUpdateResult(
                toDtoList(confirmed),
                toDtoList(rejected)
        );
    }
}
