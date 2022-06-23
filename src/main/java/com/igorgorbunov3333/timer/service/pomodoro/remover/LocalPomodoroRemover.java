package com.igorgorbunov3333.timer.service.pomodoro.remover;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.exception.NoDataException;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl.CurrentDayLocalPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

//TODO: do not use flush()? Instead use @Transactional?
@Component
@AllArgsConstructor
public class LocalPomodoroRemover {

    private final PomodoroRepository pomodoroRepository;
    private final CurrentDayLocalPomodoroProvider currentDayLocalPomodoroProvider;

    public void removeAll() {
        pomodoroRepository.deleteAll();
        pomodoroRepository.flush();
    }

    public Long removeLatest() {
        List<PomodoroDto> dailyPomodoros = currentDayLocalPomodoroProvider.provide(null);
        if (CollectionUtils.isEmpty(dailyPomodoros)) {
            throw new NoDataException("No daily pomodoros");
        }
        PomodoroDto latestDto = dailyPomodoros.get(dailyPomodoros.size() - 1);
        Long pomodoroId = latestDto.getId();
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
        return pomodoroId;
    }

    public void remove(Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new NoDataException("No such pomodoro with id [" + pomodoroId + "]"));
        LocalDate pomodoroLocalDate = pomodoro.getStartTime().toLocalDate();
        if (pomodoroLocalDate.isBefore(LocalDate.now())) {
            throw new PomodoroException("Pomodoro with id [" + pomodoroId + "] cannot be deleted because pomodoro not from todays day");
        }
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
    }

}
