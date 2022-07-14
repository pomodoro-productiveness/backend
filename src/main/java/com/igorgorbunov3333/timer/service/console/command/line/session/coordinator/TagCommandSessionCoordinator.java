package com.igorgorbunov3333.timer.service.console.command.line.session.coordinator;

import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.TagSessionProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TagCommandSessionCoordinator {

    private final Map<String, TagSessionProcessor> tagOperationByService;

    public TagCommandSessionCoordinator(List<TagSessionProcessor> tagSessionProcessors) {
        tagOperationByService = tagSessionProcessors.stream()
                .collect(Collectors.toMap(TagSessionProcessor::action, Function.identity()));
    }

    public boolean coordinate(String action, List<PomodoroTagInfo> tagsWithNumbers) {
        TagSessionProcessor processor = tagOperationByService.get(action);

        if (processor != null) {
            processor.process(tagsWithNumbers);
            return true;
        } else {
            return false;
        }
    }

}
