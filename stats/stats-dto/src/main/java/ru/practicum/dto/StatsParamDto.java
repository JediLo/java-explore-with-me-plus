package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsParamDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String uris;
    private boolean uniques;
}
