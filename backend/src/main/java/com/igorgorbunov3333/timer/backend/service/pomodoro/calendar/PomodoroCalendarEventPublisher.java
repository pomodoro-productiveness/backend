package com.igorgorbunov3333.timer.backend.service.pomodoro.calendar;

import com.igorgorbunov3333.timer.backend.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.backend.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.google.calendar.GoogleCalendarEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroCalendarEventPublisher {

    private static final String COLOR_ID_DEFAULT = "3";

    private final GoogleCalendarEventPublisher googleCalendarEventPublisher;
    private final PomodoroProperties pomodoroProperties;
    private final GoogleServicesProperties googleServicesProperties;

    public void publish(PomodoroDto pomodoro) {
        try {
            log.debug("Try to publish pomodoro pomodoro [{}] as an event to the google calendar", toPomodoroWithSystemTimezone(pomodoro));

            publishPomodoro(pomodoro);
        } catch (Exception e) {
            log.error("Exception occurred while publishing calendar event related to pomodoro", e);
        }
    }

    private PomodoroDto toPomodoroWithSystemTimezone(PomodoroDto pomodoro) {
        return new PomodoroDto(
                null,
                pomodoro.getStartTime().withZoneSameInstant(ZoneId.systemDefault()),
                pomodoro.getEndTime().withZoneSameInstant(ZoneId.systemDefault()),
                false,
                null,
                null
        );
    }

    private void publishPomodoro(PomodoroDto pomodoro) {
        String summary = getSummary(pomodoro);

        String colorId = getColorId(pomodoro.getTags());

        long pomodoroStartTime = pomodoro.getStartTime().toInstant().toEpochMilli();
        long pomodoroEndTime = pomodoro.getEndTime().toInstant().toEpochMilli();
        googleCalendarEventPublisher.publishEvent(summary, googleServicesProperties.getCalendar().getId().getPomodoro(), colorId, pomodoroStartTime, pomodoroEndTime);
    }

    private String getSummary(PomodoroDto pomodoro) {
        String summary = "Pomodoro";
        String tagTitle;
        if (!CollectionUtils.isEmpty(pomodoro.getTags())) {
            tagTitle = "#" + pomodoro.getTags().stream()
                    .map(PomodoroTagDto::getName)
                    .sorted()
                    .collect(Collectors.joining("#"));
            summary += StringUtils.SPACE + "with tag" + StringUtils.SPACE + tagTitle;
        }
        return summary;
    }

    private String getColorId(List<PomodoroTagDto> pomodoroTags) {
        String educationColorId = pomodoroProperties.getTag().getEducation().getCalendarIdColor();
        String workColorId = pomodoroProperties.getTag().getWork().getCalendarIdColor();

        String workTagName = pomodoroProperties.getTag().getWork().getName();
        String educationTagName = pomodoroProperties.getTag().getEducation().getName();

        List<String> pomodoroTagNames = getTagNames(pomodoroTags);
        String colorId;
        if (pomodoroTagNames.contains(workTagName)) {
            colorId = workColorId;
        } else if (pomodoroTagNames.contains(educationTagName)) {
            colorId = educationColorId;
        } else {
            colorId = COLOR_ID_DEFAULT;
        }
        return colorId;
    }

    private List<String> getTagNames(List<PomodoroTagDto> pomodoroTags) {
        List<String> pomodoroTagNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pomodoroTags)) {
            pomodoroTagNames = pomodoroTags.stream()
                    .map(PomodoroTagDto::getName)
                    .collect(Collectors.toList());
        }
        return pomodoroTagNames;
    }

}
