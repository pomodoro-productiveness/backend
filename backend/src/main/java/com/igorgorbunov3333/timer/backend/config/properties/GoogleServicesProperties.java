package com.igorgorbunov3333.timer.backend.config.properties;

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
@ConfigurationProperties(prefix = "google-services")
public class GoogleServicesProperties {

    @NotBlank
    private String credentialsPath;
    @NotNull
    private Calendar calendar;

    @Data
    public static class Calendar {

        @NotNull
        private Summary summary;

        @NotNull
        private CalendarId id;

    }

    @Data
    public static class Summary {

        @NotBlank
        private String dayOff;
        @NotBlank
        private String pomodoro;

    }

    @Data
    public static class CalendarId {

        @NotBlank
        private String dayOff;
        @NotBlank
        private String pomodoro;

    }

}
