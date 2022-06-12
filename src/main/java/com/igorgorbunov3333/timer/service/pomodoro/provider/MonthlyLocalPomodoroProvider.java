package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Component
@AllArgsConstructor
public class MonthlyLocalPomodoroProvider extends LocalPomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;

    public List<PomodoroDto> provide(String pomodoroTag) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate(); //TODO: extract common code

        YearMonth yearMonth = YearMonth.from(today); //TODO: extract common code
        LocalDate startDayOfMonth = yearMonth.atDay(1); //TODO: extract common code

        return provide(startDayOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()),
                today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()), pomodoroTag);
    }

}
