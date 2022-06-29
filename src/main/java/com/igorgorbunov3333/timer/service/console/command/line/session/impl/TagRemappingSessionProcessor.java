package com.igorgorbunov3333.timer.service.console.command.line.session.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.impl.AbstractPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl.CurrentDayLocalPomodoroProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
@AllArgsConstructor
public class TagRemappingSessionProcessor extends AbstractPomodoroSessionMapper implements TagSessionProcessor, NumberProvidable {

    private final CurrentDayLocalPomodoroProvider currentDayLocalPomodoroProvider;
    private final CommandProvider commandProvider;
    private final PrinterService printerService;
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;

    @Override
    public void process(List<PomodoroTagInfo> tagPositionToTags) {
        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provide(null);

        printerService.print("Chose pomodoro to remap tags");

        Map<Integer, PomodoroDto> numberToPomodoro = new HashMap<>();

        int count = 0;
        for (PomodoroDto pomodoro : dailyPomodoro) {
            printerService.printWithoutCarriageOffset(++count + DefaultPrinterService.DOT + StringUtils.SPACE);

            numberToPomodoro.put(count, pomodoro);

            printerService.printPomodoro(pomodoro, true, true);
        }

        printerService.printParagraph();

        PomodoroDto chosenPomodoro = null;
        while (true) {
            int chosenNumber = provideNumber();
            if (chosenNumber == -1) {
                return;
            }

            chosenPomodoro = numberToPomodoro.get(chosenNumber);
            if (chosenPomodoro == null) {
                printerService.print(String.format("No such pomodoro with number [%s]", chosenNumber));
            } else {
                break;
            }
        }

        startTagSessionAndPrintDailyPomodoros(chosenPomodoro.getId());
    }

    @Override
    public String action() {
        return "3";
    }

}
