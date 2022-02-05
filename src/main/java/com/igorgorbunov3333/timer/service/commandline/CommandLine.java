package com.igorgorbunov3333.timer.service.commandline;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroPeriodService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@AllArgsConstructor
public class CommandLine {

    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";
    private static final String MESSAGE_NO_POMODOROS = "No pomodoros to display!";

    private final PomodoroService pomodoroService;
    private final SecondsFormatter secondsFormatter;
    private final PomodoroPeriodService pomodoroPeriodService;

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        printFeaturesList();
        while (true) {
            String text = sc.nextLine();
            gotoChoice(text);
            if (text.equals("exit")) {
                break;
            }
        }
        sc.close();
    }

    @SneakyThrows
    private void gotoChoice(String input) {
        if (input.equals("1")) {
            pomodoroService.starPomodoro();
            System.out.println("Pomodoro has started");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1100);
                String formattedTime = getPomodoroCurrentDurationInString();
                System.out.println(formattedTime);
            }
        } else if (input.equals("2")) {
            if (pomodoroService.isNotActive()) {
                System.out.println(MESSAGE_POMODORO_NOT_STARTED);
                return;
            }
            pomodoroService.stopPomodoro();
        } else if (input.equals("3")) {
            if (pomodoroService.isNotActive()) {
                System.out.println(MESSAGE_POMODORO_NOT_STARTED);
                return;
            }
            String formattedTime = getPomodoroCurrentDurationInString();
            System.out.println(formattedTime);
        } else if (input.equals("4")) {
            System.out.println(pomodoroService.getPomodorosInDay());
        } else if (input.equals("5")) {
            List<PomodoroDto> pomodoros = pomodoroService.getPomodorosInDayExtended();
            if (pomodoros.isEmpty()) {
                System.out.println(MESSAGE_NO_POMODOROS);
                return;
            }
            printDailyPomodoros(pomodoros, true);
        } else if (input.equals("6")) {
            Map<LocalDate, List<PomodoroDto>> datesToPomadoros = pomodoroService.getMonthlyPomodoros();
            if (datesToPomadoros.isEmpty()) {
                System.out.println(MESSAGE_NO_POMODOROS);
                return;
            }
            for (Map.Entry<LocalDate, List<PomodoroDto>> entry : datesToPomadoros.entrySet()) {
                System.out.println();
                System.out.println(entry.getKey());
                System.out.println("pomodoros in day - " + entry.getValue().size());
                printDailyPomodoros(entry.getValue(), false);
            }
        } else if (input.equals("help")) {
            printFeaturesList();
        } else if (input.startsWith("remove")) {
            char[] inputChars = input.toCharArray();
            int index = "remove ".length();
            if (inputChars[index - 1] != ' ') {
                System.out.println("Incorrect input \"" + input + "\". \"remove\" and id should be separated with \" \"");
                return;
            }
            String pomodoroIdArgument = getArgumentString(input, inputChars, index);
            Long pomodoroId = Long.valueOf(pomodoroIdArgument);
            pomodoroService.removePomodoro(pomodoroId);
        } else if (input.startsWith("save")) {
            pomodoroService.save();
        } else if (input.equals("week")) {
            Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = pomodoroPeriodService.getCurrentWeekPomodoros();
            for (Map.Entry<DayOfWeek, List<PomodoroDto>> entry : weeklyPomodoros.entrySet()) {
                System.out.println();
                System.out.println(entry.getKey().toString());
                List<PomodoroDto> dailyPomodoros = entry.getValue();
                printDailyPomodoros(dailyPomodoros, false);
            }
        } else {
            System.out.println("Invalid input, please try again");
        }
    }

    private String getPomodoroCurrentDurationInString() {
        int seconds = pomodoroService.getPomodoroCurrentDuration();
        return secondsFormatter.formatInMinutes(seconds);
    }

    private void printFeaturesList() {
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

    private void printDailyPomodoros(List<PomodoroDto> pomodoros, boolean withId) {
        long pomodoroDurationInSeconds = 0;
        for (PomodoroDto pomodoro : pomodoros) {
            printPomodoro(pomodoro, withId);
            long pomodoroStartEndTimeDifference = getStartEndTimeDifferenceInSeconds(pomodoro);
            pomodoroDurationInSeconds += pomodoroStartEndTimeDifference;
        }
        System.out.println("Pomodoros amount - " + pomodoros.size());
        System.out.println("Total time - " + secondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

    private long getStartEndTimeDifferenceInSeconds(PomodoroDto pomodoro) {
        return ChronoUnit.SECONDS.between(pomodoro.getStartTime(), pomodoro.getEndTime());
    }

    private void printPomodoro(PomodoroDto pomodoro, boolean withId) {
        String pomodoroPeriod = mapTimestamp(pomodoro);
        long pomodoroStartEndTimeDifference = getStartEndTimeDifferenceInSeconds(pomodoro);
        String pomodoroDuration = secondsFormatter.formatInMinutes(pomodoroStartEndTimeDifference);
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

    private String mapTimestamp(PomodoroDto pomodoro) {
        String startTimeString = format(pomodoro.getStartTime());
        String endTimeString = format(pomodoro.getEndTime());
        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String format(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    private String getArgumentString(String input, char[] inputChars, int index) {
        char[] pomodoroIdInString = new char[input.length() - index];
        for (int i = index, j = 0; i < inputChars.length; i++, j++) {
            pomodoroIdInString[j] = inputChars[i];
        }
        return new String(pomodoroIdInString);
    }

}
