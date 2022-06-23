package com.igorgorbunov3333.timer.service.pomodoro.calendar;

import com.igorgorbunov3333.timer.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.google.calendar.GoogleCalendarEventPublisher;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroCalendarEventPublisher {

    private static final String COLOR_ID_DEFAULT = "3";

    private final GoogleCalendarEventPublisher googleCalendarEventPublisher;
    private final PomodoroProperties pomodoroProperties;
    private final GoogleServicesProperties googleServicesProperties;
    private final TagService tagService;

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
        PomodoroTagDto pomodoroTag = pomodoro.getTag();

        String summary = "Pomodoro";
        String tagName = StringUtils.EMPTY;
        if (pomodoroTag != null) {
            tagName = pomodoro.getTag().getName();
            summary += StringUtils.SPACE + "with tag" + StringUtils.SPACE + tagName;
        }

        String educationColorId = pomodoroProperties.getTag().getEducation().getCalendarIdColor();
        String workColorId = pomodoroProperties.getTag().getWork().getCalendarIdColor();

        String workTagName = pomodoroProperties.getTag().getWork().getName();
        String educationTagName = pomodoroProperties.getTag().getEducation().getName();

        String colorId;
        if (tagService.isRelative(tagName, workTagName)) {
            colorId = workColorId;
        } else if (tagService.isRelative(tagName, educationTagName)) {
            colorId = educationColorId;
        } else {
            colorId = COLOR_ID_DEFAULT;
        }

        long pomodoroStartTime = pomodoro.getStartTime().toInstant().toEpochMilli();
        long pomodoroEndTime = pomodoro.getEndTime().toInstant().toEpochMilli();

        googleCalendarEventPublisher.publishEvent(summary, googleServicesProperties.getCalendar().getId().getPomodoro(), colorId, pomodoroStartTime, pomodoroEndTime);

    }

}
