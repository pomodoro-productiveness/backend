package com.igorgorbunov3333.timer.service.commandline;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import lombok.SneakyThrows;

import java.util.List;

public interface PrinterService {

    void printFeaturesList();

    void printSavedAndDailyPomodorosAfterStoppingPomodoro(PomodoroDto savedPomodoro);

    void getAndPrintDailyPomodoros();

    void printDailyPomodoros(List<PomodoroDto> pomodoros, boolean withId);

    void printPomodorosForLastMonth();

    @SneakyThrows
    void printFirstThreeFirstPomodoroSecondsDuration();

}
