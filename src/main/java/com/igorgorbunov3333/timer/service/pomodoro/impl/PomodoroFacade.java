package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.service.pomodoro.provider.DailyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.DefaultLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.MonthlyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.remover.LocalPomodoroRemover;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroAutoSaver;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroSaver;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class PomodoroFacade {

    //TODO: create one bean instead of using different kind of local pomodoro providers
    private final LocalPomodoroRemover localPomodoroRemover;
    private final DefaultLocalPomodoroProvider defaultLocalPomodoroProvider;
    private final DailyLocalPomodoroProvider dailyLocalPomodoroProvider;
    private final PomodoroAutoSaver pomodoroAutoSaver;
    private final PomodoroSaver pomodoroSaver;
    private final MonthlyLocalPomodoroProvider monthlyLocalPomodoroProvider;
    private final LocalPomodoroUpdater localPomodoroUpdater;

    public PomodoroDto save(int pomodoroDuration, List<PomodoroPauseDto> pomodoroPauses) {
        return pomodoroSaver.save(pomodoroDuration, pomodoroPauses);
    }

    public PomodoroDto save(int pomodoroDuration) {
        return pomodoroSaver.save(pomodoroDuration);
    }

    public long getPomodorosInDay() {
        return dailyLocalPomodoroProvider.provideDailyLocalPomodoros().size();
    }

    public List<PomodoroDto> getPomodorosInDayExtended() {
        return dailyLocalPomodoroProvider.provideDailyLocalPomodoros();
    }

    public Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros() {
        return monthlyLocalPomodoroProvider.provide();
    }

    public void remove(Long pomodoroId) {
        localPomodoroRemover.remove(pomodoroId);
    }

    public Long removeLatest() {
        return localPomodoroRemover.removeLatest();
    }

    public PomodoroDto saveAutomatically() {
        return pomodoroAutoSaver.save();
    }

    public void updatePomodoroWithTag(Long pomodoroId, String tagName) {
        localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tagName);
    }

    public List<PomodoroDto> getAllSortedPomodoros() {
        return defaultLocalPomodoroProvider.provide(null, null, null);
    }

    public void removeAll() {
        localPomodoroRemover.removeAll();
    }

    public void save(List<PomodoroDto> pomodoros, List<PomodoroTag> savedTags) {
        pomodoroSaver.save(pomodoros, savedTags);
    }

}
