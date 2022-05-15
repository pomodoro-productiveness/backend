package com.igorgorbunov3333.timer.service.commandline;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.service.commandline.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroPeriodService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@AllArgsConstructor
public class CommandLine {

    private static final String INVALID_INPUT = "Invalid input, please try again";

    private final PomodoroService pomodoroService;
    private final PomodoroPeriodService pomodoroPeriodService;
    private final PomodoroEngineService pomodoroEngineService;
    private final PrinterService printerService;

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        printerService.printFeaturesList();
        while (true) {
            String text = sc.nextLine();
            try {
                gotoChoice(text);
            } catch (Exception e) {
                System.out.println(INVALID_INPUT);
            }
            if (text.equals("exit")) {
                break;
            }
        }
        sc.close();
    }

    @SneakyThrows
    private void gotoChoice(String input) {
        if (input.equals("1")) {
            startPomodoro();
        } else if (input.equals("2")) {
            stopPomodoro();
        } else if (input.equals("3")) {
            showPomodoroCurrentDuration();
        } else if (input.equals("4")) {
            System.out.println(pomodoroService.getPomodorosInDay());
        } else if (input.equals("5")) {
            getAndPrintDailyPomodoros();
        } else if (input.equals("6")) {
            getAndPrintMonthlyPomodoros();
        } else if (input.equals("help")) {
            printerService.printFeaturesList();
        } else if (input.startsWith("remove")) {
            removePomodoroById(input);
        } else if (input.startsWith("save")) {
            savePomodoroAutomatically();
        } else if (input.equals("week")) {
            getAndPrintPomodorosForCurrentWeek();
        } else if (input.equals("pause")) {
            pausePomodoro();
        } else if (input.equals("resume")) {
            pomodoroEngineService.resumePomodoro();
        } else {
            System.out.println(INVALID_INPUT);
        }
    }

    private void startPomodoro() {
        try {
            pomodoroEngineService.startPomodoro();
        } catch (PomodoroEngineException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Pomodoro has started");
        pomodoroEngineService.printFirstThreeFirstPomodoroSecondsDuration();
    }

    private void stopPomodoro() {
        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroEngineService.stopPomodoro();
        } catch (PomodoroEngineException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println(DefaultPrinterService.MESSAGE_POMODORO_SAVED + savedPomodoro);
        getAndPrintDailyPomodoros();
    }

    private void showPomodoroCurrentDuration() {
        String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();
        System.out.println(pomodoroCurrentDuration);
    }

    private void removePomodoroById(String input) {
        char[] inputChars = input.toCharArray();
        if (inputChars.length == "remove".length()) {
            List<PomodoroDto> dailyPomodoros = pomodoroService.getPomodorosInDayExtended();
            if (dailyPomodoros.isEmpty()) {
                System.out.println("Unable to remove latest pomodoro as no daily pomodoros");
                return;
            }
            Long removedPomodoroId;
            try {
                removedPomodoroId = pomodoroService.removeLatest();
            } catch (PomodoroException e) {
                System.out.println(e.getMessage());
                return;
            }
            if (removedPomodoroId != null) {
                System.out.println("Pomodoro with id " + removedPomodoroId + " successfully removed");
            }
            return;
        }
        int index = "remove ".length();
        if (inputChars[index - 1] != ' ') {
            System.out.println("Incorrect input \"" + input + "\". \"remove\" and id should be separated with \" \"");
            return;
        }
        String pomodoroIdArgument = getArgumentString(input, inputChars, index);
        Long pomodoroId = Long.valueOf(pomodoroIdArgument);
        try {
            pomodoroService.removePomodoro(pomodoroId);
        } catch (PomodoroException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Pomodoro with id [" + pomodoroId + "] removed");
    }

    private void savePomodoroAutomatically() {
        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroService.saveAutomatically();
        } catch (PomodoroException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println(DefaultPrinterService.MESSAGE_POMODORO_SAVED + savedPomodoro);
        getAndPrintDailyPomodoros();
    }

    private void getAndPrintPomodorosForCurrentWeek() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = pomodoroPeriodService.getCurrentWeekPomodoros();
        if (weeklyPomodoros.isEmpty()) {
            System.out.println("No weekly pomodoros");
        }
        printerService.printDayOfWeekToPomodoros(weeklyPomodoros);
    }

    private void getAndPrintDailyPomodoros() {
        List<PomodoroDto> pomodoros = pomodoroService.getPomodorosInDayExtended();
        printerService.printPomodorosWithIds(pomodoros);
    }

    private void getAndPrintMonthlyPomodoros() {
        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = pomodoroService.getMonthlyPomodoros();
        if (datesToPomadoros.isEmpty()) {
            System.out.println("No monthly pomodoros");
        }
        printerService.printLocalDatePomodoros(datesToPomadoros);
    }

    private void pausePomodoro() {
        pomodoroEngineService.pausePomodoro();
        System.out.println("Pomodoro paused!");
    }

    private String getArgumentString(String input, char[] inputChars, int index) {
        char[] pomodoroIdInString = new char[input.length() - index];
        for (int i = index, j = 0; i < inputChars.length; i++, j++) {
            pomodoroIdInString[j] = inputChars[i];
        }
        return new String(pomodoroIdInString);
    }

}
