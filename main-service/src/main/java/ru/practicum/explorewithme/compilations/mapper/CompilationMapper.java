package ru.practicum.explorewithme.compilations.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.compilations.dto.CompilationDto;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.model.Compilation;
import ru.practicum.explorewithme.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return new Compilation();
    }

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto();
    }

}
