package com.igorgorbunov3333.timer.console.service.dayoff;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import com.igorgorbunov3333.timer.console.rest.dto.dayoff.DayOffsSaveRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DayOffComponent {

    private final BackendRestClient backendRestClient;

    public void saveDayOffs(DayOffsSaveRequestDto saveRequest) {
        backendRestClient.post(BackendRestUtils.REST_PATH_DAY_OFFS, saveRequest);
    }

    public List<DayOffDto> getDayOffs() {
        return backendRestClient.get(
                BackendRestUtils.REST_PATH_DAY_OFFS,
                new ParameterizedTypeReference<>() {},
                Map.of());
    }

}
