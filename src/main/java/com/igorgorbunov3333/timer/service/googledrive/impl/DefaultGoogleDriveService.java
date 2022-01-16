package com.igorgorbunov3333.timer.service.googledrive.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;

@Service
public class DefaultGoogleDriveService implements GoogleDriveService {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/home/ihor/credentials.json";

    Drive service = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials())
            .setApplicationName(APPLICATION_NAME)
            .build();

    public DefaultGoogleDriveService() throws GeneralSecurityException, IOException {
    }

    @Override
    public PomodoroDataDto getPomodoroData() {
        String fileId = "1Onqbker3q6KdUnnB_5y4UVAUUXcqM5NA";
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = outputStream.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        PomodoroDataDto pomodoroDataDto = PomodoroDataDto.createEmpty();
        try {
            pomodoroDataDto = objectMapper.readValue(json, PomodoroDataDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return pomodoroDataDto;
    }

    @Override
    public void updatePomodoroData(PomodoroDataDto pomodoroData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String newFileContent = null;
        try {
            newFileContent = objectMapper.writeValueAsString(pomodoroData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        java.io.File file = new java.io.File("pomodoros.json");
        boolean created = false;
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!created) {
            System.out.println("file didn't created");
        }
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("pomodoros.json"), "utf-8"))) {
                writer.write(newFileContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File googleDocFile = new File();
        File oldFile = null;
        try {
            oldFile = service.files().get("1Onqbker3q6KdUnnB_5y4UVAUUXcqM5NA").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileMimeType = oldFile.getMimeType();
        FileContent mediaContent = new FileContent(fileMimeType, file);
        googleDocFile.setName(oldFile.getName());
        try {
            File updatedFile = service.files().update("1Onqbker3q6KdUnnB_5y4UVAUUXcqM5NA", googleDocFile, mediaContent).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean deleted = file.delete();
        if (!deleted) {
            System.out.println("Pomodoro file wasn't deleted");
        }
    }

    private static Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, DriveScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
