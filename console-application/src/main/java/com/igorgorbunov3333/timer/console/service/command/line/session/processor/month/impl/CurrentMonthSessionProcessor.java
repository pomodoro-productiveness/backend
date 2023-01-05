package com.igorgorbunov3333.timer.console.service.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.MonthlyPomodoroProvider;
import com.igorgorbunov3333.timer.console.service.printer.MonthlyPomodoroReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CurrentMonthSessionProcessor implements MonthSessionProcessor {

    private final MonthlyPomodoroReportPrinter monthlyPomodoroReportPrinter;
    private final MonthlyPomodoroProvider monthlyPomodoroProvider;

    @Override
    public void process(List<PomodoroDto> pomodoro) {
        MonthlyPomodoroDto monthlyPomodoroDto = monthlyPomodoroProvider.provideCurrentMonthPomodoro(pomodoro);

        monthlyPomodoroReportPrinter.print(monthlyPomodoroDto);

        SimplePrinter.printParagraph();
    }

    @Override
    public String action() {
        return "1";
    }

}
