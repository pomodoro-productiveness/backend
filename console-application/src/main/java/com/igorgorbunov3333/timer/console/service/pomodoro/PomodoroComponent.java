package com.igorgorbunov3333.timer.console.service.pomodoro;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroAutoSaveRequestDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroAutoSaveResponseDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroSaveRequestDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void updatePomodoroWithTag(@NonNull List<Long> pomodoroIds, @NonNull Set<String> tags) {

    }

    public PomodoroDto save(PomodoroSaveRequestDto pomodoroSaveRequestDto) {
        return restClient.post(
                BackendRestUtils.REST_PATH_POMODORO,
                PomodoroDto.class,
                pomodoroSaveRequestDto
        );
    }

    public List<PomodoroDto> saveAutomatically(PomodoroAutoSaveRequestDto autoSaveRequest) {
        PomodoroAutoSaveResponseDto response = restClient.post(
                BackendRestUtils.REST_PATH_POMODORO_AUTO_SAVE,
                PomodoroAutoSaveResponseDto.class,
                autoSaveRequest
        );

        return response.getPomodoro();
    }

    public void deletePomodoro(long pomodoroId) {
        restClient.delete(BackendRestUtils.REST_PATH_POMODORO, Map.of("pomodoroId", String.valueOf(pomodoroId)));
    }

}
