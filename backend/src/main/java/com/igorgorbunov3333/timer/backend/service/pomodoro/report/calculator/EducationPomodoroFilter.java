package com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.backend.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EducationPomodoroFilter {

    private final PomodoroProperties pomodoroProperties;

    public List<PomodoroDto> filter(List<PomodoroDto> pomodoro) {
        String educationTag = getEducationTag();

        return pomodoro.stream()
                .filter(filterByTag(educationTag))
                .collect(Collectors.toList());
    }

    private String getEducationTag() {
        return pomodoroProperties.getTag()
                .getEducation()
                .getName();
    }

    private Predicate<PomodoroDto> filterByTag(String tag) {
        return p -> p.getTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toList())
                .contains(tag);
    }

}
