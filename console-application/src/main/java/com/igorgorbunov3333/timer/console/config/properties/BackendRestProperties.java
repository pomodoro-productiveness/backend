package com.igorgorbunov3333.timer.console.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "backend.rest")
public class BackendRestProperties {

    @NotBlank private String scheme;
    @NotBlank private String host;
    @NotBlank private String basePath;

}
