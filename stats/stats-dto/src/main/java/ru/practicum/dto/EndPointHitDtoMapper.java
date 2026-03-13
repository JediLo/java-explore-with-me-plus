package ru.practicum.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.model.ViewStats;

@UtilityClass
public  class EndPointHitDtoMapper {

    public static ViewStats toViewStats(EndPointHitDto endPointHitDto) {
        return new ViewStats();
    }

    public static EndPointHitDto toEndPointHitDto(ViewStats viewStats) {
        return new EndPointHitDto();
    }
}
