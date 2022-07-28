package com.igorgorbunov3333.timer.service.console.printer.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPrinterService implements PrinterService {

    public static final String MESSAGE_POMODORO_SAVED = "Pomodoro successfully saved: ";
    public static final String MESSAGE_NO_POMODORO = "No pomodoro to display!";
    public static final String DOT = ".";
    public static final String TABULATION = "         ";

    private static final String YES_NO_QUESTION = "Yes (y), No";

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
        System.out.println("4. pomadoros today");
        System.out.println("5. pomadoros today extended");
        System.out.println("6. pomadoros for the current month");
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
    public void printPomodoro(PomodoroDto pomodoro, boolean withIdAndTag, int number, Integer longestNumberLength) {
        String pomodoroPeriod = printPomodoroStartEndTime(pomodoro);
        long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
        String pomodoroDuration = SecondsFormatter.formatInMinutes(pomodoroStartEndTimeDifference);
        String formattedPomodoroPeriodAndDuration = "time - "
                .concat(pomodoroPeriod)
                .concat(" | ")
                .concat("duration - ")
                .concat(pomodoroDuration);
        String pomodoroRow;

        if (withIdAndTag) {
            String tagLine = StringUtils.EMPTY;
            if (!CollectionUtils.isEmpty(pomodoro.getTags())) {
                tagLine = "#" + pomodoro.getTags().stream()
                        .map(PomodoroTagDto::getName)
                        .sorted()
                        .collect(Collectors.joining(" #"));
            }

            String spaces = StringUtils.SPACE;
            if (longestNumberLength != null) {
                int spacesAmount = longestNumberLength - String.valueOf(number).length();

                spaces += StringUtils.SPACE.repeat(Math.max(0, spacesAmount));
            }

            pomodoroRow = number + DOT + spaces
                    .concat(formattedPomodoroPeriodAndDuration)
                    .concat(" | ")
                    .concat("tag: ")
                    .concat(tagLine);
        } else {
            pomodoroRow = formattedPomodoroPeriodAndDuration;
        }
        System.out.println(pomodoroRow);
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

        int longestNumberLength = 0;
        if (!CollectionUtils.isEmpty(pomodoroList)) {
            longestNumberLength = String.valueOf(pomodoroList.size()).length();
        }

        int count = 0;
        for (PomodoroDto pomodoro : pomodoroList) {
            printPomodoro(pomodoro, true, ++count, longestNumberLength);
            long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
            pomodoroDurationInSeconds += pomodoroStartEndTimeDifference;
        }
        System.out.println("Pomodoro amount - " + pomodoroList.size());
        System.out.println("Total time - " + SecondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

    private String printPomodoroStartEndTime(PomodoroDto pomodoro) {
        String startTimeString = printDateTime(pomodoro.getStartTime().toLocalDateTime());
        String endTimeString = printDateTime(pomodoro.getEndTime().toLocalDateTime());
        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String printDateTime(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
