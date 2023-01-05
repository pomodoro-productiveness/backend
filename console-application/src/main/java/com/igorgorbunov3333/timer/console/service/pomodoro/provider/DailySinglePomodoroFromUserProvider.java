package com.igorgorbunov3333.timer.console.service.pomodoro.provider;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.PomodoroPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import com.igorgorbunov3333.timer.console.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DailySinglePomodoroFromUserProvider implements NumberProvidable {

    private final PomodoroComponent pomodoroComponent;
    @Getter
    private final CommandProvider commandProvider;
    private final PomodoroPrinter pomodoroPrinter;
    private final CurrentTimeComponent currentTimeComponent;

    public PomodoroDto provide() {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();
        List<PomodoroDto> dailyPomodoro = pomodoroComponent.getPomodoro(today, today, null);

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
