package ru.practicum;



import ru.practicum.model.ViewStats;

import java.util.Collection;

public class StatsClient {

    // Rest Client

    public ViewStats getStats(Collection<ViewStats> viewStats) {

        return new ViewStats();
    }

    public void hit(ViewStats viewStats) {

    }
}
