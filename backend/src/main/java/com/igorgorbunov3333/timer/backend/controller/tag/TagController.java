package com.igorgorbunov3333.timer.backend.controller.tag;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<PomodoroTagDto> getTags() {
        return tagService.getSortedTags(false);
    }

    @PostMapping
    public PomodoroTagDto save(@RequestParam String tagName) {
        return tagService.save(tagName);
    }

    @DeleteMapping
    public void deleteTag(String name) {
        tagService.delete(name);
    }

}
