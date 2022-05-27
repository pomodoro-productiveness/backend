package com.igorgorbunov3333.timer.service.console.command.processor;

import com.igorgorbunov3333.timer.service.console.command.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandProcessor {

    private final Map<String, CommandService> commandByService;

    @Autowired
    public CommandProcessor(List<CommandService> commandServices) {
        commandByService = commandServices.stream()
                .collect(Collectors.toMap(CommandService::command, Function.identity()));
    }

    public boolean process(String command) {
        String commandFirstWord = getFirstCommandFirstWord(command);
        CommandService commandService = commandByService.get(commandFirstWord);
        if (commandService == null) {
            return false;
        }
        commandService.process();
        return true;
    }

    private String getFirstCommandFirstWord(String command) {
        return command.split(" ")[0];
    }

}
