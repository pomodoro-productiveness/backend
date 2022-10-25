package com.igorgorbunov3333.timer.backend.service.console.command.line.session.coordinator;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.TagSessionProcessor;
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

    public boolean coordinate(String action, Map<Integer, PomodoroTagDto> numberedTags) {
        TagSessionProcessor processor = tagOperationByService.get(action);

        if (processor != null) {
            processor.process(numberedTags);
            return true;
        } else {
            return false;
        }
    }

}
