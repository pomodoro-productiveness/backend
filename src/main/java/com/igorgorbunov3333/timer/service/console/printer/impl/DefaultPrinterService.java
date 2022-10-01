package com.igorgorbunov3333.timer.service.console.printer.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.printer.PomodoroPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DefaultPrinterService implements PrinterService {

    private final PomodoroPrinter pomodoroPrinter;

    @Override
    public void printFeaturesList() {
        SimplePrinter.print("1. start");
        SimplePrinter.print("2. stop");
        SimplePrinter.print("3. current time");
        SimplePrinter.print("4. pomodoro today");
        SimplePrinter.print("5. pomodoro today extended");
        SimplePrinter.print("6. pomodoro for the current month");
        SimplePrinter.print("Type \"help\" to list all available features");
        SimplePrinter.print("Type \"remove\" to remove latest pomodoro or specify pomodoro id. For example \"remove 10\"");
        SimplePrinter.print("Type \"save\" for saving pomodoro automatically. Specify number after whitespace to save multiple pomodoro");
        SimplePrinter.print("Type \"week\" to list all pomodoros for current week");
        SimplePrinter.print("Type \"year\" to list all pomodoros for current year");
        SimplePrinter.print("Type \"tag\" to enter tag menu");
        SimplePrinter.print("Type \"e\" to exit");
    }

    @Override
    public void printPomodoroWithIdsAndTags(List<PomodoroDto> pomodoro) {
        printPomodoroListWithIdsAndTags(pomodoro);
    }

    @Override
    public void printDayOfWeekToPomodoro(Map<String, List<PomodoroDto>> weeklyPomodoro) {
        boolean first = true;
        for (Map.Entry<String, List<PomodoroDto>> entry : weeklyPomodoro.entrySet()) {
            if (!first) {
                SimplePrinter.printParagraph();
            } else {
                first = false;
            }
            SimplePrinter.print(entry.getKey());
            List<PomodoroDto> dailyPomodoro = entry.getValue();
            printPomodoroWithIdsAndTags(dailyPomodoro);
        }
    }

    private void printPomodoroListWithIdsAndTags(List<PomodoroDto> pomodoroList) {
        if (pomodoroList.isEmpty()) {
            SimplePrinter.print(PrintUtil.MESSAGE_NO_POMODORO);
            return;
        }
        long pomodoroDurationInSeconds = 0;

        int count = 0;
        Map<Integer, PomodoroDto> numberToPomodoro = new LinkedHashMap<>();
        for (PomodoroDto pomodoro : pomodoroList) {
            numberToPomodoro.put(++count, pomodoro);
            long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
            pomodoroDurationInSeconds += pomodoroStartEndTimeDifference;
        }

        pomodoroPrinter.print(numberToPomodoro);

        SimplePrinter.print("Pomodoro amount - " + pomodoroList.size());
        SimplePrinter.print("Total time - " + SecondsFormatter.formatInHours(pomodoroDurationInSeconds));
    }

}
