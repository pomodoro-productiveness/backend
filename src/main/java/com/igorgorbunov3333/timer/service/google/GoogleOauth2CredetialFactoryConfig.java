package com.igorgorbunov3333.timer.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.DriveScopes;
import com.igorgorbunov3333.timer.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.service.google.util.GoogleServiceUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

@Configuration
@AllArgsConstructor
public class GoogleOauth2CredetialFactoryConfig {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private final GoogleServicesProperties googleServicesProperties;

    @Bean
    @SneakyThrows
    public Credential buildCredential() {
        final String credetialsFilePath = googleServicesProperties.getCredentialsPath();
        InputStream in = new FileInputStream(credetialsFilePath);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GoogleServiceUtil.JSON_FACTORY, new InputStreamReader(in));

        Set<String> scopes = Set.of(DriveScopes.DRIVE, CalendarScopes.CALENDAR);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GoogleServiceUtil.JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

}
