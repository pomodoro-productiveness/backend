package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@AllArgsConstructor
public class DefaultLocalPomodoroProvider extends LocalPomodoroProvider {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;

}
