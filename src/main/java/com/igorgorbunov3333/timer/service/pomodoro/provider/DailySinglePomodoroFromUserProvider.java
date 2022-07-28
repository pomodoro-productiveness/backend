package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DailySinglePomodoroFromUserProvider implements NumberProvidable {

    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;

    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    public PomodoroDto provide() {
        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provide(null);

        if (CollectionUtils.isEmpty(dailyPomodoro)) {
            printerService.print("There are no daily pomodoro");
            return null;
        }

        Map<Integer, PomodoroDto> numberToPomodoro = new HashMap<>();

        int longestPomodoroNumberLength = String.valueOf(dailyPomodoro.size()).length();

        int count = 0;
        for (PomodoroDto pomodoro : dailyPomodoro) {
            numberToPomodoro.put(++count, pomodoro);

            printerService.printPomodoro(pomodoro, true, count, longestPomodoroNumberLength);
        }

        printerService.printParagraph();

        PomodoroDto chosenPomodoro;
        while (true) {
            int chosenNumber = provideNumber();
            if (chosenNumber == -1) {
                return null;
            }

            chosenPomodoro = numberToPomodoro.get(chosenNumber);
            if (chosenPomodoro == null) {
                printerService.print(String.format("No such pomodoro with number [%s]", chosenNumber));
            } else {
                return chosenPomodoro;
            }
        }
    }

}
