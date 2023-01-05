package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroAutoSaveRequestDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.console.service.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.console.service.exception.FreeSlotException;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AutoSaveCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor {

    @Getter
    private final PomodoroComponent pomodoroComponent;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final TagPomodoroSessionUpdater tagPomodoroSessionUpdater;
    private final StandardReportPrinter standardReportPrinter;
    @Getter
    private final CurrentTimeComponent currentTimeComponent;

    @Override
    public void process() {
        Integer numberOfPomodoroToSave = getNumberFromCommand();

        if (numberOfPomodoroToSave == null) {
            return;
        }

        List<PomodoroDto> savedPomodoroList = save(numberOfPomodoroToSave);

        if (CollectionUtils.isEmpty(savedPomodoroList)) {
            return;
        }

        List<Long> pomodoroIdList = savedPomodoroList.stream()
                .map(PomodoroDto::getId)
                .collect(Collectors.toList());
        startTagSessionAndPrintDailyPomodoro(pomodoroIdList);

        LocalDate currentDay = currentTimeComponent.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period);
    }

    @Override
    public String command() {
        return "save";
    }

    private Integer getNumberFromCommand() {
        String[] autoSaveCommandParts = CurrentCommandStorage.currentCommand.split(StringUtils.SPACE);

        if (autoSaveCommandParts.length > 1) {
            return parseNumber(autoSaveCommandParts[1]);
        } else {
            return 1;
        }
    }

    private Integer parseNumber(String commandNumberPart) {
        Integer pomodoroNumber = null;
        try {
            pomodoroNumber = Integer.valueOf(commandNumberPart);
            return pomodoroNumber;
        } catch (NumberFormatException e) {
            SimplePrinter.print(String.format("Incorrect number [%s]", pomodoroNumber));
            return null;
        }
    }

    private List<PomodoroDto> save(int numberToSave) {
        List<PomodoroDto> savedPomodoro;
        try {
            PomodoroAutoSaveRequestDto saveRequest = new PomodoroAutoSaveRequestDto(numberToSave, null);

            savedPomodoro = pomodoroComponent.saveAutomatically(saveRequest);

            savedPomodoro.forEach(this::printSuccessfullySavedMessage);

            return savedPomodoro;
        } catch (FreeSlotException e) {
            String errorMessage = e.getMessage();
            SimplePrinter.print(errorMessage);
            return List.of();
        }
    }

}
