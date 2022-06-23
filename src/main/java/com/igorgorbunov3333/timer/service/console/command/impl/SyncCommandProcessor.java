package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.remote.RemotePomodoroDataService;
import com.igorgorbunov3333.timer.service.synchronization.priority.remote.RemotePrioritySynchronizer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SyncCommandProcessor implements CommandProcessor {

    private final RemotePrioritySynchronizer remotePrioritySynchronizer;
    private final PrinterService printerService;
    private final RemotePomodoroDataService remotePomodoroDataService;

    @Override
    @SneakyThrows
    public void process() {
        printerService.print("Synchronization started");

        PomodoroMetadataDto remoteData = remotePomodoroDataService.provideRemoteData();
        remotePrioritySynchronizer.synchronize(remoteData);

        printerService.print("Synchronization finished");
    }

    @Override
    public String command() {
        return "sync";
    }

}
