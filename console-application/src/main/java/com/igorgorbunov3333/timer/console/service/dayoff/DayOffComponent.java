package com.igorgorbunov3333.timer.console.service.dayoff;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.dayoff.DayOffsSaveRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DayOffComponent {

    private final BackendRestClient backendRestClient;

    public void saveDayOffs(DayOffsSaveRequestDto saveRequest) {
        backendRestClient.post(BackendRestUtils.REST_PATH_DAY_OFF, saveRequest);
    }

}
