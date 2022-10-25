package com.igorgorbunov3333.timer.backend.service.pomodoro.remover;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.exception.FreeSlotException;
import com.igorgorbunov3333.timer.backend.service.exception.NoDataException;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

//TODO: do not use flush()? Instead use @Transactional?
@Component
@AllArgsConstructor
public class PomodoroRemover {

    private final PomodoroRepository pomodoroRepository;
    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;

    public void remove(Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new NoDataException("No such pomodoro with id [" + pomodoroId + "]"));
        LocalDate pomodoroLocalDate = pomodoro.getStartTime().toLocalDate();
        if (pomodoroLocalDate.isBefore(LocalDate.now())) {
            throw new FreeSlotException("Pomodoro with id [" + pomodoroId + "] cannot be deleted because pomodoro not from todays day");
        }
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
    }

}
