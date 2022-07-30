package com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.MonthlyPomodoroPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroByMonthsDivider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CurrentMonthSessionProcessor implements MonthSessionProcessor {

    private final PomodoroByMonthsDivider pomodoroByMonthsDivider;
    private final MonthlyPomodoroPrinter monthlyPomodoroPrinter;

    @Override
    public void process(List<PomodoroDto> pomodoro) {
        Map<PeriodDto, List<PomodoroDto>> periodByPomodoro = pomodoroByMonthsDivider.divide(pomodoro);

        List<Map.Entry<PeriodDto, List<PomodoroDto>>> entries = new ArrayList<>(periodByPomodoro.entrySet());
        List<PomodoroDto> latestMonthPomodoro = entries.get(entries.size() - 1).getValue();

        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = latestMonthPomodoro.stream()
                .collect(Collectors.groupingBy(p -> p.getStartTime().toLocalDate()));
        Map<LocalDate, List<PomodoroDto>> sortedPomodoros = new TreeMap<>(datesToPomadoros);
        if (sortedPomodoros.isEmpty()) {
            SimplePrinter.print("No monthly pomodoros");
            return;
        }

        List<PomodoroDto> monthlyPomodoro = sortedPomodoros.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        monthlyPomodoroPrinter.print(monthlyPomodoro);
    }

    @Override
    public String action() {
        return "1";
    }

}
