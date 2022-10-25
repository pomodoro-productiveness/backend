package com.igorgorbunov3333.timer.backend.model;

import java.time.ZonedDateTime;

public interface TemporalObject {

    ZonedDateTime getStartTime();

    ZonedDateTime getEndTime();

}
