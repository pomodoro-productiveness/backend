package com.igorgorbunov3333.timer.console.service.pomodoro;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class PomodoroComponent {

    private BackendRestClient restClient;

    public List<PomodoroDto> getPomodoro(@NonNull LocalDate start, @NonNull LocalDate end, String tag) {
        Map<String, String> queryParams = new HashMap<>(Map.of("start", start.toString(), "end", end.toString()));

        if (StringUtils.isNotBlank(tag)) {
            queryParams.put("tag", tag);
        }

        return restClient.get(
                BackendRestUtils.REST_PATH_POMODORO,
                new ParameterizedTypeReference<>() {
                },
                queryParams
        );
    }

}
