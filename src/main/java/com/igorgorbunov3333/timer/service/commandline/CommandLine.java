package com.igorgorbunov3333.timer.service.commandline;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CommandLine {

    private final PomodoroService pomodoroService;
    private final SecondsFormatter secondsFormatter;

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Pomodoro application started");
        System.out.println();
        printFeaturesList();
        while (true) {
            String text = sc.nextLine();
            gotoChoice(text);
            if (text.equals("exit"))
                break;
        }
        sc.close();
    }

    private void gotoChoice(String input) {
        if (input.equals("1")) {
            pomodoroService.starPomodoro();
        } else if (input.equals("2")) {
            pomodoroService.stopPomodoro();
        } else if (input.equals("3")) {
            int seconds = pomodoroService.getPomodoroTime();
            String formattedTime = secondsFormatter.formatInMinutes(seconds);
            System.out.println(formattedTime);
        } else if (input.equals("4")) {
            System.out.println(pomodoroService.getPomodorosInDay());
        } else if (input.equals("5")) {
            List<Pomodoro> pomodoros = pomodoroService.getPomodorosInDayExtended();
            printDailyPomodoros(pomodoros, true);
        } else if (input.equals("6")) {
            Map<LocalDate, List<Pomodoro>> datesToPomadoros = pomodoroService.getPomodorosInMonthExtended();
            for (Map.Entry<LocalDate, List<Pomodoro>> entry : datesToPomadoros.entrySet()) {
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
        } else if (input.startsWith("save ")) {
            char[] inputChars = input.toCharArray();
            int index = "save ".length();
            if (inputChars[index - 1] != ' ') {
                System.out.println("Incorrect input \"" + input + "\". \"save\" and timestamps should be separated with \" \"");
                return;
            }
            String pomodoroTimestampsArgument = getArgumentString(input, inputChars, index);
            String[] timestampStrings = pomodoroTimestampsArgument.split("#");
            List<String> trimmedTimestampStrings = Arrays.stream(timestampStrings)
                    .map(String::trim)
                    .collect(Collectors.toList());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime firstLocalDateTime = LocalDateTime.parse(trimmedTimestampStrings.get(0), formatter);
            LocalDateTime secondLocalDateTime = LocalDateTime.parse(trimmedTimestampStrings.get(1), formatter);
            Pomodoro pomodoroToSave;
            if (firstLocalDateTime.isBefore(secondLocalDateTime)) {
                pomodoroToSave = new Pomodoro(null, firstLocalDateTime, secondLocalDateTime);
            } else {
                pomodoroToSave = new Pomodoro(null, secondLocalDateTime, firstLocalDateTime);
            }
            pomodoroService.save(pomodoroToSave);
        } else {
            System.out.println("Invalid input, please try again");
        }
    }

    private void printFeaturesList() {
        System.out.println("1. start");
        System.out.println("2. stop");
        System.out.println("3. current time");
        System.out.println("4. pomadoros today");
        System.out.println("5. pomadoros today extended");
        System.out.println("6. pomadoros for the last month");
        System.out.println("7. remove pomodoro by id. For example \"remove 10\"");
        System.out.println("8. save pomodoro. For example \"save 2022-01-20T09:00:00#2022-01-20T09:20:00\"");
    }

    private void printDailyPomodoros(List<Pomodoro> pomodoros, boolean withId) {
        long pomodoroDurationInSeconds = 0;
        for (Pomodoro pomodoro : pomodoros) {
            printPomodoro(pomodoro, withId);
            pomodoroDurationInSeconds += pomodoro.getStartEndTimeDifferenceInSeconds();
        }
        System.out.println("Total time - " + secondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

    private void printPomodoro(Pomodoro pomodoro, boolean withId) {
        String pomodoroPeriod = mapTimestamp(pomodoro);
        String pomodoroDuration = secondsFormatter.formatInMinutes(pomodoro.getStartEndTimeDifferenceInSeconds());
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

    private String mapTimestamp(Pomodoro pomodoro) {
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
