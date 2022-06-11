package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.igorgorbunov3333.timer.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.service.pomodoro.RemotePomodoroDataService;
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

//TODO: use PrinterService
@Service
@AllArgsConstructor
public class DefaultRemotePomodoroDataService implements RemotePomodoroDataService {  //TODO: rename

    private final GoogleServicesProperties googleServicesProperties;
    private final Drive service;

    @Override
    @SneakyThrows
    public PomodoroDataDto getRemoteData() {
        String fileId = googleServicesProperties.getDocumentId();
        OutputStream outputStream = new ByteArrayOutputStream();
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
    public void updateRemoteData(PomodoroDataDto pomodoroData) {
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
        final String documentId = googleServicesProperties.getDocumentId();
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
