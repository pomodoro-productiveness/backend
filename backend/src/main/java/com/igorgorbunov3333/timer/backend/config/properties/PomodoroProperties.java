package com.igorgorbunov3333.timer.backend.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "pomodoro")
public class PomodoroProperties {

    @NotNull
    @Positive
    private Integer minimumLifetime;
    @NotNull
    @Positive
    private Integer pomodorosPerDay;
    @NotNull
    private Standard standard;
    @NotNull
    @Positive
    private Integer duration;

    @NotNull
    @Positive
    private Integer automaticShutdownDuration;

    @Data
    public static class Standard {

        @NotNull
        @Positive
        private Integer work;
        @NotNull
        @Positive
        private Integer education;

    }

}
