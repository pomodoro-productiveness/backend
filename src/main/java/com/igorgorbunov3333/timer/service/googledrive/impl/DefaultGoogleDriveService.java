package com.igorgorbunov3333.timer.service.googledrive.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.igorgorbunov3333.timer.config.properties.GoogleDriveProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveCredentialsProvider;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class DefaultGoogleDriveService implements GoogleDriveService {  //TODO: rename

    private final GoogleDriveProperties googleDriveProperties;
    private final GoogleDriveCredentialsProvider credentialsProvider;

    @Override
    @SneakyThrows
    public PomodoroDataDto getPomodoroData() {
        String fileId = googleDriveProperties.getDocumentId();
        OutputStream outputStream = new ByteArrayOutputStream();
        Drive service = credentialsProvider.getAuthorizedGoogleDriveService();
        if (service == null) {
            System.out.println("Cannot get google drive service");
            return PomodoroDataDto.createEmpty();
        }
        service.files()
                .get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        String json = outputStream.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, PomodoroDataDto.class);
    }

    @Override
    @SneakyThrows
    public void updatePomodoroData(PomodoroDataDto pomodoroData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String newFileContent = objectMapper.writeValueAsString(pomodoroData);
        java.io.File file = new java.io.File("pomodoros.json");
        boolean created = file.createNewFile();
        if (!created) {
            System.out.println("file didn't created");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("pomodoros.json"), StandardCharsets.UTF_8))) {
            writer.write(newFileContent);
        }
        File googleDocFile = new File();
        final String documentId = googleDriveProperties.getDocumentId();
        Drive service = credentialsProvider.getAuthorizedGoogleDriveService();
        if (service == null) {
            System.out.println("Cannot get google drive service");
            return;
        }
        File oldFile = service.files().get(documentId).execute();
        FileContent mediaContent = new FileContent(oldFile.getMimeType(), file);
        googleDocFile.setName(oldFile.getName());
        service.files()
                .update(documentId, googleDocFile, mediaContent)
                .execute();
        boolean deleted = file.delete();
        if (!deleted) {
            System.out.println("Pomodoro file wasn't deleted");
        }
    }

}
