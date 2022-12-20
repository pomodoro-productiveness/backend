package com.igorgorbunov3333.timer.console.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "pomodoro")
public class PomodoroProperties {

    @NotNull private Tag tag;

    @Data
    public static class Tag {

        @NotBlank private TagDescription work;
        @NotBlank private TagDescription education;

    }

    @Data
    public static class TagDescription {

        @NotBlank private String name;
        @NotBlank private String calendarIdColor;

    }

}
