package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StartPomodoroCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void process() {
        try {
            pomodoroEngineService.startPomodoro();
        } catch (PomodoroEngineException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Pomodoro has started");
        pomodoroEngineService.printFirstThreeFirstPomodoroSecondsDuration();
    }

    @Override
    public String command() {
        return "1";
    }

}
