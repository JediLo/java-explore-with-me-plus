package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.dto.EndPointHitDto;

@SpringBootApplication
public class StatsService {
    public static void main(String[] args) {
        SpringApplication.run(StatsService.class, args);
    }
}
