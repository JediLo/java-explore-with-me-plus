package ru.practicum.explorewithme.compilations.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateCompilationRequest {
    // Обязательно в этом списке должны быть уникальные события
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
