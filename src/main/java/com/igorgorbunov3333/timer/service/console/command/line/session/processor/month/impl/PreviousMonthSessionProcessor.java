package com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
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
    @Getter
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
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

        printerService.print("Choose month to display");

        int count = 0;
        Map<Integer, Map.Entry<PeriodDto, List<PomodoroDto>>> numberedPeriodsByMonthlyPomodoro = new LinkedHashMap<>();
        for (Map.Entry<PeriodDto, List<PomodoroDto>> entry : periodByPomodoro.entrySet()) {
            printerService.print(++count + DefaultPrinterService.DOT + StringUtils.SPACE
                    + Month.from(entry.getKey().getStart())
                    .getDisplayName(TextStyle.FULL, Locale.getDefault())
                    .toUpperCase());

            numberedPeriodsByMonthlyPomodoro.put(count, entry);
        }

        while (true) {
            int numberAnswer = provideNumber();

            if (numberAnswer == -1) {
                return;
            }

            Map.Entry<PeriodDto, List<PomodoroDto>> chosenPeriodToMonthlyPomodoro = numberedPeriodsByMonthlyPomodoro.get(numberAnswer);

            if (chosenPeriodToMonthlyPomodoro != null) {
                standardReportPrinter.print(chosenPeriodToMonthlyPomodoro.getKey(), chosenPeriodToMonthlyPomodoro.getValue());
                tagDurationReportPrinter.print(chosenPeriodToMonthlyPomodoro.getValue());
                break;
            } else {
                printerService.print(String.format("No month under the number %d", numberAnswer));
            }
        }
    }

    @Override
    public String action() {
        return "2";
    }

}
