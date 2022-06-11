package com.igorgorbunov3333.timer.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.drive.Drive;
import com.igorgorbunov3333.timer.service.google.util.GoogleServiceUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DriveFactoryConfig {

    private final Credential credential;

    @Bean
    @SneakyThrows
    public Drive buildDrive() {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), GoogleServiceUtil.JSON_FACTORY, credential)
                .setApplicationName(GoogleServiceUtil.APPLICATION_NAME)
                .build();
    }

}
