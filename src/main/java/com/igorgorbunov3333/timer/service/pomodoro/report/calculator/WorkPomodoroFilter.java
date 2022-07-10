package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WorkPomodoroFilter {

    private final PomodoroProperties pomodoroProperties;

    public List<PomodoroDto> filter(List<PomodoroDto> pomodoro) {
        String workPomodoroTag = getPomodoroWorkTag();

        return pomodoro.stream()
                .filter(filterByTag(workPomodoroTag))
                .collect(Collectors.toList());
    }

    private String getPomodoroWorkTag() {
        return pomodoroProperties.getTag()
                .getWork()
                .getName();
    }

    private Predicate<PomodoroDto> filterByTag(String workTag) {
        return p -> p.getTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toList())
                .contains(workTag);
    }

}
