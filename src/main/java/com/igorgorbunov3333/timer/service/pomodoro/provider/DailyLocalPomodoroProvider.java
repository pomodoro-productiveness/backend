package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyLocalPomodoroProvider extends LocalPomodoroProvider {

    @Getter
    private final PomodoroRepository pomodoroRepository;
    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroMapper pomodoroMapper;

    @Transactional(readOnly = true)
    public List<PomodoroDto> provideDailyLocalPomodoros() {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();

        return provide(startEndTimePair.getFirst(), startEndTimePair.getSecond(), null);
    }

}
