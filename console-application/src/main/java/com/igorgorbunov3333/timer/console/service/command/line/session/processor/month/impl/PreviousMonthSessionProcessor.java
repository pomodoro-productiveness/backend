package com.igorgorbunov3333.timer.console.service.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.console.service.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.period.PomodoroByMonthsDivider;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.MonthlyPomodoroProvider;
import com.igorgorbunov3333.timer.console.service.printer.MonthlyPomodoroReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PreviousMonthSessionProcessor implements MonthSessionProcessor, NumberProvidable {

    private final PomodoroByMonthsDivider pomodoroByMonthsDivider;
    private final MonthlyPomodoroReportPrinter monthlyPomodoroReportPrinter;
    @Getter
    private final CommandProvider commandProvider;
    private final MonthlyPomodoroProvider monthlyPomodoroProvider;

    @Override
    public void process(List<PomodoroDto> pomodoro) {
        Map<YearMonth, List<PomodoroDto>> monthByPomodoro = pomodoroByMonthsDivider.divide(pomodoro);

        monthByPomodoro.remove(
                monthByPomodoro.keySet().stream()
                        .sorted(Comparator.naturalOrder())
                        .collect(Collectors.toList())
                        .get(monthByPomodoro.size() - 1)
        );

        SimplePrinter.printParagraph();
        SimplePrinter.print("Choose month to display");
        SimplePrinter.printParagraph();

        int count = 0;
        Map<Integer, Map.Entry<YearMonth, List<PomodoroDto>>> numberedPeriodsByMonthlyPomodoro = new LinkedHashMap<>();
        for (Map.Entry<YearMonth, List<PomodoroDto>> entry : monthByPomodoro.entrySet()) {
            SimplePrinter.print(++count + PrintUtil.DOT + StringUtils.SPACE
                    + entry.getKey().getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.getDefault())
                    .toUpperCase());

            numberedPeriodsByMonthlyPomodoro.put(count, entry);
        }

        SimplePrinter.printParagraph();

        while (true) {
            int numberAnswer = provideNumber();

            if (numberAnswer == -1) {
                return;
            }

            Map.Entry<YearMonth, List<PomodoroDto>> chosenPeriodToMonthlyPomodoro = numberedPeriodsByMonthlyPomodoro.get(numberAnswer);

            if (chosenPeriodToMonthlyPomodoro != null) {
                MonthlyPomodoroDto monthlyPomodoroDto = monthlyPomodoroProvider.providePomodoroForMonth(
                        chosenPeriodToMonthlyPomodoro.getKey(),
                        chosenPeriodToMonthlyPomodoro.getValue()
                );

                monthlyPomodoroReportPrinter.print(monthlyPomodoroDto);
                break;
            } else {
                SimplePrinter.print(String.format("No month under the number %d", numberAnswer));
            }
        }
    }

    @Override
    public String action() {
        return "2";
    }

}
