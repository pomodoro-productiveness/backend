package com.igorgorbunov3333.timer.console.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "audio-file")
public class AudioFileProperties {

    @NotBlank
    private String path;

}