package ru.practicum.explorewithme.compilations.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.compilations.dto.CompilationDto;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.model.Compilation;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.mapper.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        compilation.setEvents(events);
        return compilation;
    }

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream()
                        .map(event -> EventMapper.toEventShortDto(event, 0L, 0L))
                        .collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
