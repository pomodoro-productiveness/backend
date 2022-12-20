package com.igorgorbunov3333.timer.console.service.tag;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagSaveRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class TagComponent {

    private final BackendRestClient backendRestClient;

    public void saveTag(String tag) {
        backendRestClient.post(BackendRestUtils.REST_PATH_TAGS, new PomodoroTagSaveRequestDto(Set.of(tag)));
    }

    public List<PomodoroTagDto> getAllTags() {
        return backendRestClient.get(BackendRestUtils.REST_PATH_TAGS, new ParameterizedTypeReference<>() {}, Map.of());
    }

}
