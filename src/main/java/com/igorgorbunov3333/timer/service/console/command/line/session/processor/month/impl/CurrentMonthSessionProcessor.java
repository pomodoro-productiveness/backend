package com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.MonthlyPomodoroReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.provider.MonthlyPomodoroProvider;
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
        MonthlyPomodoroDto monthlyPomodoroDto = monthlyPomodoroProvider.provideCurrentMonthlyPomodoro(pomodoro);

        monthlyPomodoroReportPrinter.print(monthlyPomodoroDto);

        SimplePrinter.printParagraph();
    }

    @Override
    public String action() {
        return "1";
    }

}
