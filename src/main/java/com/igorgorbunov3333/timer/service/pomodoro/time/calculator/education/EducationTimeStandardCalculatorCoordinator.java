package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education;

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
public class EducationTimeStandardCalculatorCoordinator {

    private final Map<PomodoroPeriod, EducationTimeStandardCalculator> calculatorsByPeriods;

    @Autowired
    public EducationTimeStandardCalculatorCoordinator(List<EducationTimeStandardCalculator> calculators) {
        this.calculatorsByPeriods = calculators.stream()
                .collect(Collectors.toMap(EducationTimeStandardCalculator::period, Function.identity()));
    }

    public int calculate(PomodoroPeriod period, List<PomodoroDto> pomodoro) {
        EducationTimeStandardCalculator calculator = calculatorsByPeriods.get(period);

        if (calculator == null) {
            throw new IllegalArgumentException(String.format("Wrong period: %s", period)); //TODO: handle this exception
        }

        return calculator.calculate(pomodoro);
    }

}
