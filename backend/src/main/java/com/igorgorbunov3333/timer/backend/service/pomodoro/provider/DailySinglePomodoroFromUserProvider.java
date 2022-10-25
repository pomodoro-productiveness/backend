package com.igorgorbunov3333.timer.backend.service.pomodoro.provider;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.backend.service.console.printer.PomodoroPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DailySinglePomodoroFromUserProvider implements NumberProvidable {

    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;

    @Getter
    private final CommandProvider commandProvider;
    private final PomodoroPrinter pomodoroPrinter;

    public PomodoroDto provide() {
        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provideForCurrentDay(null);

        if (CollectionUtils.isEmpty(dailyPomodoro)) {
            SimplePrinter.print("There are no daily pomodoro");
            return null;
        }

        Map<Integer, PomodoroDto> numberToPomodoro = NumberToItemBuilder.build(dailyPomodoro);

        pomodoroPrinter.print(numberToPomodoro);

        SimplePrinter.printParagraph();

        PomodoroDto chosenPomodoro;
        while (true) {
            int chosenNumber = provideNumber();
            if (chosenNumber == -1) {
                return null;
            }

            chosenPomodoro = numberToPomodoro.get(chosenNumber);
            if (chosenPomodoro == null) {
                SimplePrinter.print(String.format("No such pomodoro with number [%s]", chosenNumber));
            } else {
                return chosenPomodoro;
            }
        }
    }

}
