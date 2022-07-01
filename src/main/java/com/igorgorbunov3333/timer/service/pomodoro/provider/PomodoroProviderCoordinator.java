package com.igorgorbunov3333.timer.service.pomodoro.provider;

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
public class PomodoroProviderCoordinator {

    private final Map<PomodoroPeriod, PomodoroProvider> pomodoroPeriodsToProviders;

    @Autowired
    public PomodoroProviderCoordinator(List<PomodoroProvider> pomodoroProviders) {
        this.pomodoroPeriodsToProviders = pomodoroProviders.stream()
                .collect(Collectors.toMap(PomodoroProvider::pomodoroPeriod, Function.identity()));
    }

    public List<PomodoroDto> provide(PomodoroPeriod period, String tag) {
        PomodoroProvider provider = pomodoroPeriodsToProviders.get(period);

        if (provider == null) {
            throw new IllegalArgumentException(String.format("Provider not found for period %s", period));
        }

        return provider.provide(tag);
    }

}
