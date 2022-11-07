package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroPause;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PomodoroMapper {

    @Mapping(source = "pomodoroTagGroup", target = "tags", qualifiedByName = "tagGroupToTags")
    @Mapping(source = "pomodoroPauses", target = "pomodoroPauses", qualifiedByName = "pomodoroPausesDtosToEntities")
    PomodoroDto toDto(Pomodoro entity);

    @Named("tagGroupToTags")
    static List<PomodoroTagDto> tagGroupToTags(PomodoroTagGroup tagGroup) {
        List<PomodoroTagDto> tags = new ArrayList<>();

        if (tagGroup == null) {
            return tags;
        }

        Set<PomodoroTag> pomodoroTags = tagGroup.getPomodoroTags();

        if (CollectionUtils.isEmpty(pomodoroTags)) {
            return tags;
        }

        for (PomodoroTag tag : pomodoroTags) {
            PomodoroTagDto tagDto = new PomodoroTagDto(null, tag.getName(), tag.isRemoved());
            tags.add(tagDto);
        }

        return tags;
    }

    @Named("pomodoroPausesDtosToEntities")
    static List<PomodoroPauseDto> toPomodoroPauseDtos(List<PomodoroPause> entities) {
        List<PomodoroPauseDto> pauses = new ArrayList<>();

        if (CollectionUtils.isEmpty(entities)) {
            return pauses;
        }

        for (PomodoroPause pause : entities) {
            PomodoroPauseDto pauseDto = new PomodoroPauseDto(pause.getStartTime(), pause.getEndTime(), pause.getPomodoroId());
            pauses.add(pauseDto);
        }

        return pauses;
    }

}
