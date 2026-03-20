package ru.practicum.explorewithme.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.model.Request;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto();
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        return new ArrayList<>();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> confirmed,
                                                                                  List<Request> rejected) {
        return new EventRequestStatusUpdateResult();
    }
}
