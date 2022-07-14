package com.igorgorbunov3333.timer.service.console.command.line.session.coordinator;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MonthSessionCommandCoordinator {

    private final Map<String, MonthSessionProcessor> monthSessionOptionToProcessors;

    public MonthSessionCommandCoordinator(List<MonthSessionProcessor> monthSessionProcessors) {
        this.monthSessionOptionToProcessors = monthSessionProcessors.stream()
                .collect(Collectors.toMap(MonthSessionProcessor::action, Function.identity()));
    }

    public boolean coordinate(String action, List<PomodoroDto> pomodoro) {
        MonthSessionProcessor processor = monthSessionOptionToProcessors.get(action);

        if (processor != null) {
            processor.process(pomodoro);
            return true;
        } else {
            return false;
        }
    }

}
