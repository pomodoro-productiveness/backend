package com.igorgorbunov3333.timer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "audio-player")
public class AudioPlayerProperties {

    @NotBlank
    private String path;

}
