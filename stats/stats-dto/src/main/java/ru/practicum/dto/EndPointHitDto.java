package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPointHitDto {
    private int id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timeRequest;

    public ViewStats toEntity(EndPointHitDto dto) {
        ViewStats viewStats = new ViewStats();
        viewStats.setApp(dto.getApp());
        viewStats.setUri(dto.getUri());
        viewStats.setIp(dto.getIp());
        viewStats.setHitTime(dto.getTimeRequest());
        return viewStats;
    }

}
