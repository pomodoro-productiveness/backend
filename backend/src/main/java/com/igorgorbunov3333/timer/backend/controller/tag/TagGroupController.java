package com.igorgorbunov3333.timer.backend.controller.tag;

import com.igorgorbunov3333.timer.backend.controller.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.backend.service.tag.group.PomodoroTagGroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON)
public class TagGroupController {

    private final PomodoroTagGroupService pomodoroTagGroupService;

    @GetMapping(path = "/tag-groups")
    public List<PomodoroTagGroupDto> getTagGroups() {
        return pomodoroTagGroupService.getLatestTagGroups();
    }

}
