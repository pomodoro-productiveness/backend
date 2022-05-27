package com.igorgorbunov3333.timer.service.tag.impl;

import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import com.igorgorbunov3333.timer.repository.TagRepository;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultTagService implements TagService {

    private final TagRepository tagRepository;

    @Override
    public String saveTag(String name) {
        PomodoroTag tag = new PomodoroTag(null, name);
        PomodoroTag savedTag = tagRepository.save(tag);

        return savedTag.getName();
    }

}
