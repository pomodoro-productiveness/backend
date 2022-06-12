package com.igorgorbunov3333.timer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
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
    private Tag tag;

    @Setter
    @Getter
    public static class Standard {

        @NotNull
        @Positive
        private Integer work;

        @NotNull
        @Positive
        private Integer education;

    }

    @Setter
    @Getter
    public static class Tag {

        @NotBlank
        private String work;

        @NotBlank
        private String education;

    }

}
