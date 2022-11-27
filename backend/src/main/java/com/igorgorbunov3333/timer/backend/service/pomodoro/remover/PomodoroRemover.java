package com.igorgorbunov3333.timer.backend.service.pomodoro.remover;

import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PomodoroRemover {

    private final PomodoroRepository pomodoroRepository;
    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;

    public void remove(long pomodoroId) {
        boolean pomodoroExists = pomodoroRepository.existsById(pomodoroId);

        if (!pomodoroExists) {
            throw new EntityDoesNotExist(String.format("Pomodoro with id [%d] does not exist", pomodoroId));
        }

        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
    }

}
