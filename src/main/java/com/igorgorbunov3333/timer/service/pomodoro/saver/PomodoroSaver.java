package com.igorgorbunov3333.timer.service.pomodoro.saver;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroPause;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
@AllArgsConstructor
public class PomodoroSaver implements SinglePomodoroSavable {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;

    public PomodoroDto save(int pomodoroDuration, List<PomodoroPauseDto> pomodoroPauses) {
        Pomodoro pomodoro = buildPomodoro(pomodoroDuration, pomodoroPauses);
        return save(pomodoro);
    }

    public PomodoroDto save(int pomodoroDuration) {
        Pomodoro pomodoro = buildPomodoro(pomodoroDuration, List.of());
        return save(pomodoro);
    }

    private Pomodoro buildPomodoro(int pomodoroSecondsDuration, List<PomodoroPauseDto> pauses) {
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime endTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .atZone(currentZoneId);
        ZonedDateTime startTime = endTime.minusSeconds(pomodoroSecondsDuration);

        //TODO: add mapper to map dto to entity
        List<PomodoroPause> pomodoroPauses = pauses.stream()
                .map(pauseDto -> new PomodoroPause(null, pauseDto.getStartTime(), pauseDto.getEndTime(), null))
                .collect(Collectors.toList());

        return new Pomodoro(null, startTime, endTime, false, pomodoroPauses, null, null);
    }

}
