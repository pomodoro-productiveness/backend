package com.igorgorbunov3333.timer.backend.service.console.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandCoordinator {

    private final Map<String, CommandProcessor> commandByService;

    @Autowired
    public CommandCoordinator(List<CommandProcessor> commandProcessors) {
        commandByService = commandProcessors.stream()
                .collect(Collectors.toMap(CommandProcessor::command, Function.identity()));
    }

    public boolean coordinate(String command) {
        String commandFirstWord = getFirstCommandFirstWord(command);
        CommandProcessor commandProcessor = commandByService.get(commandFirstWord);
        if (commandProcessor == null) {
            return false;
        }
        commandProcessor.process();

        return true;
    }

    private String getFirstCommandFirstWord(String command) {
        return command.split(" ")[0];
    }

}
