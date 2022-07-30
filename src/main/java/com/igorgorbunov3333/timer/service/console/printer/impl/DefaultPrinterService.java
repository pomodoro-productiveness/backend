package com.igorgorbunov3333.timer.service.console.printer.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.printer.PomodoroPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DefaultPrinterService implements PrinterService {

    public static final String MESSAGE_POMODORO_SAVED = "Pomodoro successfully saved: ";
    public static final String MESSAGE_NO_POMODORO = "No pomodoro to display!";
    public static final String DOT = ".";
    public static final String TABULATION = "         ";

    private static final String YES_NO_QUESTION = "Yes (y), No";

    private final PomodoroPrinter pomodoroPrinter;

    @Override
    public void print(@NonNull String message) {
        System.out.println(message);
    }

    @Override
    public void printWithoutCarriageOffset(String message) {
        System.out.print(message);
    }

    @Override
    public void printParagraph() {
        System.out.println();
    }

    @Override
    public void printFeaturesList() {
        System.out.println("1. start");
        System.out.println("2. stop");
        System.out.println("3. current time");
        System.out.println("4. pomodoro today");
        System.out.println("5. pomodoro today extended");
        System.out.println("6. pomodoro for the current month");
        System.out.println("Type \"help\" to list all available features");
        System.out.println("Type \"remove\" to remove latest pomodoro or specify pomodoro id. For example \"remove 10\"");
        System.out.println("Type \"save\" for saving pomodoro automatically. Specify number after whitespace to save multiple pomodoro");
        System.out.println("Type \"week\" to list all pomodoros for current week");
        System.out.println("Type \"year\" to list all pomodoros for current year");
        System.out.println("Type \"tag\" to enter tag menu");
        System.out.println("Type \"sync\" to synchronize local data");
        System.out.println("Type \"e\" to exit");
    }

    @Override
    public void printSavedAndDailyPomodoroAfterStoppingPomodoro(PomodoroDto savedPomodoro,
                                                                List<PomodoroDto> dailyPomodoro) {
        System.out.println("Pomodoro stopped automatically!");
        System.out.println(MESSAGE_POMODORO_SAVED + savedPomodoro);
        printPomodoroListWithIdsAndTags(dailyPomodoro);
    }

    @Override
    public void printPomodoroWithIdsAndTags(List<PomodoroDto> pomodoro) {
        printPomodoroListWithIdsAndTags(pomodoro);
    }

    @Override
    public void printDayOfWeekToPomodoro(Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoro) {
        for (Map.Entry<DayOfWeek, List<PomodoroDto>> entry : weeklyPomodoro.entrySet()) {
            System.out.println();
            System.out.println(entry.getKey().toString());
            List<PomodoroDto> dailyPomodoros = entry.getValue();
            printPomodoroWithIdsAndTags(dailyPomodoros);
        }
    }

    @Override
    public void printYesNoQuestion() {
        print(YES_NO_QUESTION);
    }

    private void printPomodoroListWithIdsAndTags(List<PomodoroDto> pomodoroList) {
        if (pomodoroList.isEmpty()) {
            System.out.println(MESSAGE_NO_POMODORO);
            return;
        }
        long pomodoroDurationInSeconds = 0;

        printParagraph();

        int count = 0;
        Map<Integer, PomodoroDto> numberToPomodoro = new LinkedHashMap<>();
        for (PomodoroDto pomodoro : pomodoroList) {
            numberToPomodoro.put(++count, pomodoro);
            long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
            pomodoroDurationInSeconds += pomodoroStartEndTimeDifference;
        }

        pomodoroPrinter.print(numberToPomodoro);

        System.out.println("Pomodoro amount - " + pomodoroList.size());
        System.out.println("Total time - " + SecondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

}
