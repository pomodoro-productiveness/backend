package com.igorgorbunov3333.timer.console.service.message;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.message.MessageDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@AllArgsConstructor
public class MessageComponent {

    private final BackendRestClient backendRestClient;

    public boolean existsByDate(@NonNull LocalDate date) {
        return backendRestClient.get(BackendRestUtils.REST_PATH_MESSAGE, Boolean.class, Map.of("date", date.toString()));
    }

    public void save(MessageDto messageDto) {
        backendRestClient.post(BackendRestUtils.REST_PATH_MESSAGE, messageDto);
    }

}
