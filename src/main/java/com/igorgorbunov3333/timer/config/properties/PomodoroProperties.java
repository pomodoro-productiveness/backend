package com.igorgorbunov3333.timer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "pomodoro")
public class PomodoroProperties {

    private Long minimumLifetime;

}
