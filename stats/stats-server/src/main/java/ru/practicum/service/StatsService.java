package ru.practicum.service;

import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.StatsParamDto;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {

    Collection<ViewStats> getStats(StatsParamDto params);

    void saveHit(EndPointHitDto endPointHitDto);
}
