package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.StatsParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatisticHitMapper;
import ru.practicum.repository.StatisticHitRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatsService {

    private final StatisticHitRepository statisticHitRepository;
    private final StatisticHitMapper statisticHitMapper;

    @Override
    public void saveHit(EndPointHitDto hitDto) {
        statisticHitRepository.save(statisticHitMapper.toEntity(hitDto));
    }

    @Override
    public Collection<ViewStatsDto> getStats(StatsParamDto paramDto) {
        if (paramDto.getStart() == null || paramDto.getEnd() == null) {
            throw new IllegalArgumentException("start and end must not be null");
        }
        if (paramDto.getStart().isAfter(paramDto.getEnd())) {
            throw new IllegalArgumentException("start must not be after end");
        }

        boolean unique = paramDto.isUnique();
        List<String> uris = paramDto.getUris();

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statisticHitRepository.getUniqueStats(paramDto.getStart(), paramDto.getEnd());
            }
            return statisticHitRepository.getStats(paramDto.getStart(), paramDto.getEnd());
        }

        if (unique) {
            return statisticHitRepository.getUniqueStatsByUris(paramDto.getStart(), paramDto.getEnd(), uris);
        }
        return statisticHitRepository.getStatsByUris(paramDto.getStart(), paramDto.getEnd(), uris);
    }
}
