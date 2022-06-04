package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagCommandSessionStarter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagCommandProcessor implements CommandProcessor {

    private final TagCommandSessionStarter tagCommandSessionStarter;

    @Override
    public void process() {
        boolean sessionFinished = false;

        while (!sessionFinished) {
            sessionFinished = tagCommandSessionStarter.startSession();
        }
    }

    @Override
    public String command() {
        return "tag";
    }

}
