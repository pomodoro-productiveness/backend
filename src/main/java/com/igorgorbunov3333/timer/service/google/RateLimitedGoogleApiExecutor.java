package com.igorgorbunov3333.timer.service.google;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.services.drive.Drive;
import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

@Slf4j
@Component
@AllArgsConstructor
public class RateLimitedGoogleApiExecutor {

    private final RateLimiter rateLimiter = RateLimiter.create(5.0);

    @SneakyThrows
    public <T> T execute(AbstractGoogleJsonClientRequest<T> request) {
        rateLimiter.acquire();

        String serviceSimpleName = request.getAbstractGoogleClient().getClass().getSimpleName();
        log.debug("Started to process request with [{}] service", serviceSimpleName);

        T t = request.execute();

        log.debug("Request submitted successfully");

        return t;
    }

    @SneakyThrows
    public void executeMediaAndDownloadTo(Drive service, String fileId, OutputStream outputStream) {
        rateLimiter.acquire();

        log.debug("Started to process request with [Drive] service");

        service.files()
                .get(fileId)
                .executeMediaAndDownloadTo(outputStream);

        log.debug("Drive service request successfully submitted");
    }

}
