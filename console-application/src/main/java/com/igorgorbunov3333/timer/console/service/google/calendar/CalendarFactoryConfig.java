package com.igorgorbunov3333.timer.console.service.google.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.igorgorbunov3333.timer.console.service.google.util.GoogleServiceUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CalendarFactoryConfig {

    private final Credential credential;

    @Bean
    @SneakyThrows
    public Calendar buildCalendar() {
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), GoogleServiceUtil.JSON_FACTORY, credential)
                .setApplicationName(GoogleServiceUtil.APPLICATION_NAME)
                .build();
    }
}
