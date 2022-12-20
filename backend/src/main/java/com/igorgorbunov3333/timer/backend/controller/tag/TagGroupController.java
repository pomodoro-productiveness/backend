package com.igorgorbunov3333.timer.backend.controller.tag;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagSaveRequestDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.TagGroupUpdateRequestDto;
import com.igorgorbunov3333.timer.backend.service.tag.group.PomodoroTagGroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/tag-groups")
public class TagGroupController {

    private final PomodoroTagGroupService pomodoroTagGroupService;

    @GetMapping
    public List<PomodoroTagGroupDto> getTagGroups() {
        return pomodoroTagGroupService.getTagGroupsOrderedByFrequencyOfUse();
    }

    @PostMapping
    public PomodoroTagGroupDto save(@RequestBody PomodoroTagSaveRequestDto tagGroupSaveRequest) {
        return pomodoroTagGroupService.saveTagGroup(tagGroupSaveRequest.getTags());
    }

    @PutMapping
    public PomodoroTagGroupDto updateTagGroupWithTags(@RequestBody TagGroupUpdateRequestDto tagGroupUpdateRequest) {
        return pomodoroTagGroupService.updateTagGroupWithTags(tagGroupUpdateRequest.getTagGroupId(), tagGroupUpdateRequest.getTags());
    }

}
