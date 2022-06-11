package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroPause;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.TagRepository;
import com.igorgorbunov3333.timer.service.exception.NoDataException;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.DailyPomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroAutoSaveService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class DefaultPomodoroService implements PomodoroService {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final CurrentTimeService currentTimeService;
    private final DailyPomodoroService dailyPomodoroService;
    private final PomodoroAutoSaveService pomodoroAutoSaveService;
    private final TagRepository tagRepository;

    @Override
    public PomodoroDto saveByDurationWithPauses(int pomodoroDuration, List<PomodoroPauseDto> pomodoroPauses) {
        Pomodoro pomodoro = buildPomodoro(pomodoroDuration, pomodoroPauses);
        return savePomodoroAndAddSynchronizationJob(pomodoro);
    }

    @Override
    public PomodoroDto saveByDuration(int pomodoroDuration) {
        Pomodoro pomodoro = buildPomodoro(pomodoroDuration, List.of());
        return savePomodoroAndAddSynchronizationJob(pomodoro);
    }

    @Override
    public long getPomodorosInDay() {
        return dailyPomodoroService.getDailyPomodoros().size();
    }

    @Override
    public List<PomodoroDto> getPomodorosInDayExtended() {
        return dailyPomodoroService.getDailyPomodoros();
    }

    @Override
    public Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros() {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();
        List<Pomodoro> pomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(
                startEndTimePair.getFirst().minusMonths(1), startEndTimePair.getSecond());
        List<PomodoroDto> pomodoroDtos = pomodoroMapper.mapToDto(pomodoros);
        Map<LocalDate, List<PomodoroDto>> pomodorosByDates = pomodoroDtos.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));
        return new TreeMap<>(pomodorosByDates);
    }

    @Override
    public void removePomodoro(Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new NoDataException("No such pomodoro with id [" + pomodoroId + "]"));
        LocalDate pomodoroLocalDate = pomodoro.getStartTime().toLocalDate();
        if (pomodoroLocalDate.isBefore(LocalDate.now())) {
            throw new PomodoroException("Pomodoro with id [" + pomodoroId + "] cannot be deleted because pomodoro not from todays day");
        }
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
    }

    @Override
    public Long removeLatest() {
        List<PomodoroDto> dailyPomodoros = getPomodorosInDayExtended();
        if (dailyPomodoros.isEmpty()) {
            throw new NoDataException("No daily pomodoros");
        }
        PomodoroDto latestDto = dailyPomodoros.get(dailyPomodoros.size() - 1);
        Long pomodoroId = latestDto.getId();
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
        return pomodoroId;
    }

    @Override
    public PomodoroDto saveAutomatically() {
        return pomodoroAutoSaveService.save();
    }

    @Override
    public void updatePomodoroWithTag(Long pomodoroId, String tagName) {
        Pomodoro pomodoroWithTag = pomodoroRepository.getById(pomodoroId);
        PomodoroTag tag = tagRepository.findByName(tagName).orElse(null);

        if (tag == null) {
            throw new IllegalArgumentException(String.format("No tag with name %s", tagName)); //TODO: use another exception?
        }

        pomodoroWithTag.setTag(tag);
        pomodoroRepository.save(pomodoroWithTag);
    }

    @Override
    public List<PomodoroDto> getAllSortedPomodoros() {
        List<Pomodoro> pomodoros = pomodoroRepository.findAll().stream()
                .sorted(Comparator.comparing(Pomodoro::getStartTime))
                .collect(Collectors.toList());
        return pomodoroMapper.mapToDto(pomodoros);
    }

    @Override
    public void removeAllPomodoros() {
        pomodoroRepository.deleteAll();
        pomodoroRepository.flush();
    }

    @Override
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

    private PomodoroDto savePomodoroAndAddSynchronizationJob(Pomodoro pomodoro) {
        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoro);
        return pomodoroMapper.mapToDto(savedPomodoro);
    }

}
