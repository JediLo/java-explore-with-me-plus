package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.ViewStats;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPointHitDto {

 public ViewStats toViewStats(EndPointHitDto endPointHitDto) {
     return new ViewStats();
 }

 public EndPointHitDto toEndPointHitDto(ViewStats viewStats) {
     return new EndPointHitDto();
 }
}
