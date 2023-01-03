package com.igorgorbunov3333.timer.console.service.tag.group;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagGroupDto;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

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

    public void updateTagGroup() {

    }

}
