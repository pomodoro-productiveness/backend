package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.dto.tag.SingleTagDurationDto;
import com.igorgorbunov3333.timer.model.dto.tag.TagDurationReportDto;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagDurationReporter {

    public TagDurationReportDto report(List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return new TagDurationReportDto(List.of());
        }

        Set<String> tags = new HashSet<>();
        for (PomodoroDto pomodoroDto : pomodoro) {
            tags.addAll(pomodoroDto.getTags().stream().map(PomodoroTagDto::getName).collect(Collectors.toSet()));
        }

        List<SingleTagDurationDto> tagInfo = new ArrayList<>();
        for (String tag : tags) {
            List<PomodoroDto> tagPomodoro = pomodoro.stream()
                    .filter(p -> p.getTags().stream()
                            .map(PomodoroTagDto::getName)
                            .collect(Collectors.toSet())
                            .contains(tag))
                    .collect(Collectors.toList());

            long durationInSeconds = 0L;
            for (PomodoroDto currentPomodoro : tagPomodoro) {
                durationInSeconds += PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(currentPomodoro);
            }

            String durationInHours = SecondsFormatter.formatInHours(durationInSeconds);

            tagInfo.add(new SingleTagDurationDto(tag, durationInHours));
        }

        return new TagDurationReportDto(tagInfo);
    }

}
