package com.igorgorbunov3333.timer.backend.service.util;

import com.igorgorbunov3333.timer.backend.model.TemporalObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PomodoroChronoUtil {

    public static final long POMODORO_DURATION = 20L;

    public static long getStartEndTimeDifferenceInSeconds(TemporalObject temporalObject) {
        return ChronoUnit.SECONDS.between(temporalObject.getStartTime(), temporalObject.getEndTime());
    }

}
