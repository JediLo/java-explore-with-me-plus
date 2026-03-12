package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.ViewStats;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {



    public ViewStats toViewStats(ViewStatsDto viewStatsDto) {
        return new ViewStats();
    }

    public ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto();
    }
}
