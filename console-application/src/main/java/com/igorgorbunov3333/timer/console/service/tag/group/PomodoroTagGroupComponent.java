package com.igorgorbunov3333.timer.console.service.tag.group;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagSaveRequestDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class PomodoroTagGroupComponent {

    private final BackendRestClient backendRestClient;

    public List<PomodoroTagGroupDto> getTagGroups() {
        return backendRestClient.get(
                BackendRestUtils.REST_PATH_TAG_GROUPS,
                new ParameterizedTypeReference<>() {
                },
                new HashMap<>()
        );
    }

    public void updateOrderNumber(long tagGroupId) {
        String tagGroupIdSegment = String.valueOf(tagGroupId);
        String relativeUrlPath = String.join("/", BackendRestUtils.REST_PATH_TAG_GROUPS, tagGroupIdSegment);
        backendRestClient.put(relativeUrlPath, null);
    }

    public void save(@NonNull Set<String> tags) {
        PomodoroTagSaveRequestDto requestDto = new PomodoroTagSaveRequestDto(tags);

        backendRestClient.post(BackendRestUtils.REST_PATH_TAG_GROUPS, PomodoroTagGroupDto.class, requestDto);
    }

}
