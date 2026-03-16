package ru.practicum.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.model.ViewStats;

@UtilityClass
public class EndPointHitDtoMapper {

    public static ViewStats toViewStats(EndPointHitDto endPointHitDto) {
        ViewStats viewStats = new ViewStats();
        viewStats.setApp(endPointHitDto.getApp());
        viewStats.setUri(endPointHitDto.getUri());
        viewStats.setIp(endPointHitDto.getIp());
        viewStats.setHitTime(endPointHitDto.getTimestamp());
        return viewStats;
    }

    public static EndPointHitDto toEndPointHitDto(ViewStats viewStats) {
        EndPointHitDto endPointHitDto = new EndPointHitDto();
        endPointHitDto.setApp(viewStats.getApp());
        endPointHitDto.setUri(viewStats.getUri());
        endPointHitDto.setIp(viewStats.getIp());
        endPointHitDto.setTimestamp(viewStats.getHitTime());
        return new EndPointHitDto();
    }
}
