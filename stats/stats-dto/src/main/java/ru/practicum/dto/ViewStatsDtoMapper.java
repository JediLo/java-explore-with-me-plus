package ru.practicum.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.model.ViewStats;

@UtilityClass
public class ViewStatsDtoMapper {

    public static ViewStats toViewStats(ViewStatsDto viewStatsDto) {
        return new ViewStats();
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto();
    }
}
