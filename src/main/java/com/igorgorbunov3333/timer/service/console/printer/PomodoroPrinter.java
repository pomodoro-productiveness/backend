package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroPrinter {

    private final ListOfItemsPrinter listOfItemsPrinter;

    public void print(Map<Integer, PomodoroDto> numberToPomodoro) {
        Function<PomodoroDto, String> pomodoroExtractorFunction = this::mapToString;

        listOfItemsPrinter.print(numberToPomodoro, pomodoroExtractorFunction);
    }

    private String mapToString(PomodoroDto pomodoro) {
        String pomodoroPeriod = buildPomodoroStartEndTime(pomodoro);
        long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
        String pomodoroDuration = SecondsFormatter.formatInMinutes(pomodoroStartEndTimeDifference);
        String formattedPomodoroPeriodAndDuration = "time - "
                .concat(pomodoroPeriod)
                .concat(" | ")
                .concat("duration - ")
                .concat(pomodoroDuration);

        String tagLine = StringUtils.EMPTY;
        if (!CollectionUtils.isEmpty(pomodoro.getTags())) {
            tagLine = "#" + pomodoro.getTags().stream()
                    .map(PomodoroTagDto::getName)
                    .sorted()
                    .collect(Collectors.joining(" #"));
        }

        return formattedPomodoroPeriodAndDuration
                .concat(" | ")
                .concat("tag: ")
                .concat(tagLine);
    }

    private String buildPomodoroStartEndTime(PomodoroDto pomodoro) {
        String startTimeString = buildDateTime(pomodoro.getStartTime().toLocalDateTime());
        String endTimeString = buildDateTime(pomodoro.getEndTime().toLocalDateTime());

        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String buildDateTime(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
