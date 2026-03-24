package ru.practicum.explorewithme.request.dto;

import lombok.*;
import ru.practicum.explorewithme.request.model.UpdateRequestStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private UpdateRequestStatus status;
}
