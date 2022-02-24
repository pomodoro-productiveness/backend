package com.igorgorbunov3333.timer.service.commandline.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.service.commandline.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DefaultPrinterService implements PrinterService {

    public static final String MESSAGE_POMODORO_SAVED = "Pomodoro successfully saved: ";
    public static final String MESSAGE_NO_POMODOROS = "No pomodoros to display!";

    private final PomodoroService pomodoroService;
    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void printFeaturesList() {
        System.out.println("1. start");
        System.out.println("2. stop");
        System.out.println("3. current time");
        System.out.println("4. pomadoros today");
        System.out.println("5. pomadoros today extended");
        System.out.println("6. pomadoros for the last month");
        System.out.println("Type \"help\" to list all available features");
        System.out.println("remove pomodoro by id. For example \"remove 10\"");
        System.out.println("save pomodoro. For example \"save\"");
        System.out.println("list all pomodoros for current week. For example \"week\"");
    }

    @Override
    public void printSavedAndDailyPomodorosAfterStoppingPomodoro(PomodoroDto savedPomodoro) {
        System.out.println("Pomodoro stopped automatically!");
        System.out.println(MESSAGE_POMODORO_SAVED + savedPomodoro);
        getAndPrintDailyPomodoros();
    }

    @Override
    public void getAndPrintDailyPomodoros() {
        List<PomodoroDto> pomodoros = pomodoroService.getPomodorosInDayExtended();
        printDailyPomodoros(pomodoros, true);
    }

    @Override
    public void printPomodorosForLastMonth() {
        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = pomodoroService.getMonthlyPomodoros();
        if (datesToPomadoros.isEmpty()) {
            System.out.println(DefaultPrinterService.MESSAGE_POMODORO_SAVED);
            return;
        }
        for (Map.Entry<LocalDate, List<PomodoroDto>> entry : datesToPomadoros.entrySet()) {
            System.out.println();
            System.out.println(entry.getKey());
            System.out.println("pomodoros in day - " + entry.getValue().size());
            printDailyPomodoros(entry.getValue(), false);
        }
    }

    @Override
    @SneakyThrows
    public void printFirstThreeFirstPomodoroSecondsDuration() {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1100);
            String formattedTime = pomodoroEngineService.getPomodoroCurrentDurationInString();
            System.out.println(formattedTime);
        }
    }

    @Override
    public void printDailyPomodoros(List<PomodoroDto> pomodoros, boolean withId) {
        if (pomodoros.isEmpty()) {
            System.out.println(MESSAGE_NO_POMODOROS);
            return;
        }
        long pomodoroDurationInSeconds = 0;
        for (PomodoroDto pomodoro : pomodoros) {
            printPomodoro(pomodoro, withId);
            long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
            pomodoroDurationInSeconds += pomodoroStartEndTimeDifference;
        }
        System.out.println("Pomodoros amount - " + pomodoros.size());
        System.out.println("Total time - " + SecondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

    private void printPomodoro(PomodoroDto pomodoro, boolean withId) {
        String pomodoroPeriod = printPomodoroStartEndTime(pomodoro);
        long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
        String pomodoroDuration = SecondsFormatter.formatInMinutes(pomodoroStartEndTimeDifference);
        String formattedPomodoroPeriodAndDuration = "time - "
                .concat(pomodoroPeriod)
                .concat(" | ")
                .concat("duration - ")
                .concat(pomodoroDuration);
        String pomodoroRow;
        if (withId) {
            String pomodoroId = pomodoro.getId().toString();
            pomodoroRow = "id - ".concat(pomodoroId)
                    .concat(" | ")
                    .concat(formattedPomodoroPeriodAndDuration);
        } else {
            pomodoroRow = formattedPomodoroPeriodAndDuration;
        }
        System.out.println(pomodoroRow);
    }

    private String printPomodoroStartEndTime(PomodoroDto pomodoro) {
        String startTimeString = printDateTime(pomodoro.getStartTime());
        String endTimeString = printDateTime(pomodoro.getEndTime());
        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String printDateTime(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
