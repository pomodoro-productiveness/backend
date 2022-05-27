package com.igorgorbunov3333.timer.service.console.command.line.provider;

import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import com.igorgorbunov3333.timer.repository.TagRepository;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO: single responsibility principle corrupted?
@Component
@AllArgsConstructor
public class TagProvider extends AbstractLineProvider {

    private final TagRepository tagRepository;
    private final PrinterService printerService;

    public Optional<PomodoroTag> provideTag() {
        List<PomodoroTag> tags = tagRepository.findAll();
        tags.sort(Comparator.comparing(PomodoroTag::getName));

        printerService.print("Please choose tag for saved Pomodoro: ");

        Map<Integer, PomodoroTag> tagsMap = new LinkedHashMap<>();
        int counter = 0;
        for (PomodoroTag tag : tags) {
            printerService.print(++counter + " " + tag.getName());
            tagsMap.put(counter, tag);
        }

        printerService.print(++counter + " none");

        String tagNumberString = provideLine();
        Integer tagNumber = Integer.valueOf(tagNumberString);
        PomodoroTag selectedTag = tagsMap.get(tagNumber);

        return Optional.ofNullable(selectedTag);
    }

}
