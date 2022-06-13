package com.igorgorbunov3333.timer.service.console.command;

import com.igorgorbunov3333.timer.service.synchronization.toggler.LocalPrioritySynchronizationToggler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandCoordinator {

    private final Map<String, CommandProcessor> commandByService;
    private final LocalPrioritySynchronizationToggler toggler;

    @Autowired
    public CommandCoordinator(List<CommandProcessor> commandProcessors, LocalPrioritySynchronizationToggler toggler) {
        commandByService = commandProcessors.stream()
                .collect(Collectors.toMap(CommandProcessor::command, Function.identity()));
        this.toggler = toggler;
    }

    @Transactional
    public boolean coordinate(String command) {
        String commandFirstWord = getFirstCommandFirstWord(command);
        CommandProcessor commandProcessor = commandByService.get(commandFirstWord);
        if (commandProcessor == null) {
            return false;
        }
        commandProcessor.process();
        toggler.synchronize();
        return true;
    }

    private String getFirstCommandFirstWord(String command) {
        return command.split(" ")[0];
    }

}
