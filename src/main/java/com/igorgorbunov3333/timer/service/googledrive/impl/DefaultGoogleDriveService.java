package com.igorgorbunov3333.timer.service.googledrive.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.igorgorbunov3333.timer.config.properties.GoogleDriveProperties;
import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveCredentialsProvider;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

@Service
@AllArgsConstructor
public class DefaultGoogleDriveService implements GoogleDriveService {

    private final GoogleDriveProperties googleDriveProperties;
    private final GoogleDriveCredentialsProvider credentialsProvider;

    @Override
    public PomodoroDataDto getPomodoroData() {
        String fileId = googleDriveProperties.getDocumentId();
        OutputStream outputStream = new ByteArrayOutputStream();
        Drive service = credentialsProvider.getAuthorizedGoogleDriveService();
        if (service == null) {
            System.out.println("Cannot get google drive service");
            return PomodoroDataDto.createEmpty();
        }
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
            System.out.println("Error while deserialization json from google document: " + e.getMessage());
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
        final String documentId = googleDriveProperties.getDocumentId();
        Drive service = credentialsProvider.getAuthorizedGoogleDriveService();
        if (service == null) {
            System.out.println("Cannot get google drive service");
            return;
        }
        try {
            oldFile = service.files().get(documentId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileMimeType = oldFile.getMimeType();
        FileContent mediaContent = new FileContent(fileMimeType, file);
        googleDocFile.setName(oldFile.getName());
        try {
            File updatedFile = service.files().update(documentId, googleDocFile, mediaContent).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean deleted = file.delete();
        if (!deleted) {
            System.out.println("Pomodoro file wasn't deleted");
        }
    }



}
