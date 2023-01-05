package com.igorgorbunov3333.timer.backend.controller.tag;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.tag.PomodoroTagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/tags")
public class PomodoroTagController {

    private final PomodoroTagService pomodoroTagService;

    @GetMapping
    public List<PomodoroTagDto> getTags() {
        return pomodoroTagService.getSortedTags(false);
    }

    @GetMapping("/{tagName}")
    public PomodoroTagDto getTag(@PathVariable @NotBlank String tagName) {
        return pomodoroTagService.getPomodoroTag(tagName);
    }

    @PostMapping
    public PomodoroTagDto save(@RequestParam @NotBlank String tagName) {
        return pomodoroTagService.save(tagName);
    }

    @DeleteMapping
    public void deleteTag(@RequestParam @NotBlank String tagName) {
        pomodoroTagService.delete(tagName);
    }

}
