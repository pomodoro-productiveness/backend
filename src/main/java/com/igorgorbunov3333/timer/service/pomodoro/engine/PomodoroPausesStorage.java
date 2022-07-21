package com.igorgorbunov3333.timer.service.pomodoro.engine;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class PomodoroPausesStorage {

    private List<Pair<Long, Long>> pauses = new LinkedList<>();

    public void add(Pair<Long, Long> pause) {
        pauses.add(pause);
    }

    public List<Pair<ZonedDateTime, ZonedDateTime>> getPauses() {
        List<Pair<ZonedDateTime, ZonedDateTime>> zonedDateTimePauses = new ArrayList<>();

        for (Pair<Long, Long> pause : this.pauses) {
            Instant instantStart = Instant.ofEpochMilli(pause.getFirst());
            ZonedDateTime zonedDateTimeStart = instantStart.atZone(ZoneId.systemDefault());

            Instant instantEnd = Instant.ofEpochMilli(pause.getSecond());
            ZonedDateTime zonedDateTimeEnd = instantEnd.atZone(ZoneId.systemDefault());

            zonedDateTimePauses.add(Pair.of(zonedDateTimeStart, zonedDateTimeEnd));
        }

        return zonedDateTimePauses;
    }

    public long getPausesDurationInSeconds() {
        return getPauses().stream()
                .mapToLong(pause -> ChronoUnit.SECONDS.between(pause.getFirst(), pause.getSecond()))
                .sum();
    }

    public void evict() {
        pauses.clear();
    }

}
