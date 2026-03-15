package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(@Value("${stats-server.url:http://stats-server:9090}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .rootUri(serverUrl)
                .build();
    }

    public void saveHit(EndPointHitDto hitDto) {
        restTemplate.postForEntity("/hit", hitDto, Void.class);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }
        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                builder.build().toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
