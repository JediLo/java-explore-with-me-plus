package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPointHitDto {
        private long id;

        @NotBlank
        private String app;

        @NotBlank
        private String uri;

        @NotBlank
        private String ip;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull
        private LocalDateTime timestamp;
}
