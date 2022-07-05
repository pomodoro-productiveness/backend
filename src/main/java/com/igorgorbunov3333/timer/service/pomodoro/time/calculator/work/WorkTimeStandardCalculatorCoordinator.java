package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WorkTimeStandardCalculatorCoordinator {

    private final Map<PomodoroPeriod, WorkTimeStandardCalculator> calculatorsByPeriod;

    @Autowired
    public WorkTimeStandardCalculatorCoordinator(List<WorkTimeStandardCalculator> calculators) {
        this.calculatorsByPeriod = calculators.stream()
                .collect(Collectors.toMap(WorkTimeStandardCalculator::period, Function.identity()));
    }

    public int calculate(PomodoroPeriod period, List<PomodoroDto> pomodoro) {
        WorkTimeStandardCalculator calculator = calculatorsByPeriod.get(period);
        if (calculator == null) {
            throw new IllegalArgumentException(String.format("Wrong period: %s", period.name())); //TODO: handle this exception
        }

        return calculator.calculate(pomodoro);
    }

}
