package com.igorgorbunov3333.timer.backend.service.console.command.impl;

import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.TagCommandSessionStarter;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagCommandProcessor implements CommandProcessor {

    private final TagCommandSessionStarter tagCommandSessionStarter;
    @Getter
    private final TagService tagService;

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
