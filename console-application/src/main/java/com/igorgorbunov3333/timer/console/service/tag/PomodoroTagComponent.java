package com.igorgorbunov3333.timer.console.service.tag;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class PomodoroTagComponent {

    private final BackendRestClient backendRestClient;

    public void save(String tag) {
        backendRestClient.post(BackendRestUtils.REST_PATH_TAGS, PomodoroTagDto.class, tag);
    }

    public List<PomodoroTagDto> getAllTags() {
        return backendRestClient.get(BackendRestUtils.REST_PATH_TAGS, new ParameterizedTypeReference<>() {}, Map.of());
    }

    public PomodoroTagDto getByTagName(@NonNull String tagName) {
        if (StringUtils.isBlank(tagName)) {
            throw new IllegalArgumentException("Tag name should not be null");
        }

        return backendRestClient.get(
                BackendRestUtils.REST_PATH_TAGS,
                PomodoroTagDto.class,
                Map.of("tagName", tagName)
        );
    }

    public boolean exists(String tagName) {
        String relativePath = String.join("/", BackendRestUtils.REST_PATH_TAGS, tagName);

        PomodoroTagDto tag = backendRestClient.get(relativePath, PomodoroTagDto.class, new HashMap<>());

        return tag != null;
    }

    public void deleteTag(@NonNull String tagName) {
        backendRestClient.delete(BackendRestUtils.REST_PATH_TAGS, Map.of("tagName", tagName));
    }

}
