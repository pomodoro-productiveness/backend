package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education;

import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
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

    private final Map<CalculationPeriod, EducationTimeStandardCalculator> calculatorsByPeriods;

    @Autowired
    public EducationTimeStandardCalculatorCoordinator(List<EducationTimeStandardCalculator> calculators) {
        this.calculatorsByPeriods = calculators.stream()
                .collect(Collectors.toMap(EducationTimeStandardCalculator::period, Function.identity()));
    }

    public int calculate(CalculationPeriod period) {
        EducationTimeStandardCalculator calculator = calculatorsByPeriods.get(period);

        if (calculator == null) {
            throw new IllegalArgumentException(String.format("Wrong period: %s", period)); //TODO: handle this exception
        }

        return calculator.calculate();
    }

}
