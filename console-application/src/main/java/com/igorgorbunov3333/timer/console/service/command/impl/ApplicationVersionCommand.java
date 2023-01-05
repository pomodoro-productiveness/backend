package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ApplicationVersionCommand implements CommandProcessor {

    private final BuildProperties buildProperties;

    @Override
    public void process() {
        SimplePrinter.print(buildProperties.getVersion());
    }

    @Override
    public String command() {
        return "version";
    }

}
