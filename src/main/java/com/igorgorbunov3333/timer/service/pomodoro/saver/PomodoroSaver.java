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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Component
@AllArgsConstructor
public class PomodoroSaver implements SinglePomodoroSavable {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;

    public void save(List<PomodoroDto> pomodoros, List<PomodoroTag> savedTags) {
        List<Pomodoro> pomodorosToSave = pomodoroMapper.mapToEntity(pomodoros);

        List<PomodoroTag> allChildTags = savedTags.stream()
                .flatMap(tag -> tag.getChildren() == null ? Stream.of() : tag.getChildren().stream())
                .collect(Collectors.toList());
        savedTags.addAll(allChildTags);
        Map<String, PomodoroTag> tagNamesToTags = savedTags.stream()
                .collect(Collectors.toMap(PomodoroTag::getName, Function.identity()));

        for (Pomodoro pomodoro : pomodorosToSave) {
            PomodoroTag pomodoroTag = pomodoro.getTag();

            String tagName = null;
            if (pomodoroTag != null) {
                tagName = pomodoroTag.getName();
            }

            PomodoroTag savedTag = null;
            if (tagName != null) {
                savedTag = tagNamesToTags.get(tagName);
            }

            pomodoro.setTag(savedTag);
        }

        pomodoroRepository.saveAll(pomodorosToSave);
    }

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

        return new Pomodoro(null, startTime, endTime, false, pomodoroPauses, null);
    }

}
