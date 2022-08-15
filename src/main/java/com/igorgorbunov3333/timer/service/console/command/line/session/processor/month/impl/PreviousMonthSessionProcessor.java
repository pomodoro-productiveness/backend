package com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.MonthlyPomodoroReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroByMonthsDivider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Month;
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

    @Override
    public void process(List<PomodoroDto> pomodoro) {
        Map<PeriodDto, List<PomodoroDto>> periodByPomodoro = pomodoroByMonthsDivider.divide(pomodoro);

        periodByPomodoro.remove(
                periodByPomodoro.keySet().stream()
                        .sorted(Comparator.comparing(PeriodDto::getStart))
                        .collect(Collectors.toList())
                        .get(periodByPomodoro.size() - 1)
        );

        SimplePrinter.printParagraph();
        SimplePrinter.print("Choose month to display");
        SimplePrinter.printParagraph();

        int count = 0;
        Map<Integer, Map.Entry<PeriodDto, List<PomodoroDto>>> numberedPeriodsByMonthlyPomodoro = new LinkedHashMap<>();
        for (Map.Entry<PeriodDto, List<PomodoroDto>> entry : periodByPomodoro.entrySet()) {
            SimplePrinter.print(++count + PrintUtil.DOT + StringUtils.SPACE
                    + Month.from(entry.getKey().getStart())
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

            Map.Entry<PeriodDto, List<PomodoroDto>> chosenPeriodToMonthlyPomodoro = numberedPeriodsByMonthlyPomodoro.get(numberAnswer);

            if (chosenPeriodToMonthlyPomodoro != null) {
                monthlyPomodoroReportPrinter.print(chosenPeriodToMonthlyPomodoro.getKey(), chosenPeriodToMonthlyPomodoro.getValue());
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
