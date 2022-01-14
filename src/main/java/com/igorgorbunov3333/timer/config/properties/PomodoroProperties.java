package com.igorgorbunov3333.timer.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pomodoro")
public class PomodoroProperties {

    private Long minimumLifetime;

    public Long getMinimumLifetime() {
        return minimumLifetime;
    }

    public void setMinimumLifetime(Long minimumLifetime) {
        this.minimumLifetime = minimumLifetime;
    }

}
